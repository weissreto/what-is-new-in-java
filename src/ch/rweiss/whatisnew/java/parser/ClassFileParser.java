package ch.rweiss.whatisnew.java.parser;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import ch.rweiss.whatisnew.java.model.ApiArgument;
import ch.rweiss.whatisnew.java.model.ApiClass;
import ch.rweiss.whatisnew.java.model.ApiConstructor;
import ch.rweiss.whatisnew.java.model.ApiField;
import ch.rweiss.whatisnew.java.model.ApiMethod;
import ch.rweiss.whatisnew.java.model.ApiModifier;
import ch.rweiss.whatisnew.java.model.Version;

public class ClassFileParser
{
  private static final Pattern SINCE_PATTERN = Pattern
          .compile("<dt><span class=\"simpleTagLabel\">Since:<\\/span><\\/dt>[\n\r]+<dd>([0-9\\.]+)<\\/dd>", Pattern.MULTILINE);
  private static final Pattern MEMBER_SIGNATURE_PATTERN = Pattern.compile("<div class=\"memberSignature\">(.*?)<\\/div>", Pattern.DOTALL+Pattern.MULTILINE);
  private static final Pattern TYPE_LINK_PATTERN = Pattern.compile("<a [^>]*?title=\"(interface|class) in (.*?)\">");
  private static final Pattern HTML_TAGS_PATTERN = Pattern.compile("<[^>]*>");
  private static final Pattern ANNOTATION_PATTERNS = Pattern.compile("@\\p{Alnum}+(\\(.*?\\))?");

  private Path file;
  private Path relativePath;

  public ClassFileParser(Path file, Path relativePath)
  {
    this.file = file;
    this.relativePath = relativePath;
  }

  public ApiClass parse() throws IOException
  {
    String content = Files.readString(file);
	List<Signature> signatures = parseSignatures(content);
    Version classSince = parseSinceAndAssignToSignature(content, signatures);

    List<ApiField> fields = toFields(signatures);
    List<ApiMethod> methods = toMethods(signatures);    
    List<ApiConstructor> constructors = toConstructors(signatures);
    
    String className = getClassName();
    return new ApiClass(className, constructors, methods,
            fields, classSince);
  }

  private String getClassName() 
  {
	String relativePathStr = relativePath.toString();
    relativePathStr = StringUtils.removeEnd(relativePathStr, ".html");
    String className = StringUtils.replace(relativePathStr, FileSystems.getDefault().getSeparator(), ".");
	return className;
  }
 
  private Version parseSinceAndAssignToSignature(String content, List<Signature> signatures) 
  {
	Version classSince = Version.UNDEFINED;
    Matcher matcher = SINCE_PATTERN.matcher(content);
    while (matcher.find())
    {
      int sinceStart = matcher.start();
      String since = matcher.group(1);
      Signature previousTag = null;
      for (Signature tag : signatures)
      {
        if (tag.startPos < sinceStart)
        {
          previousTag = tag;
        }
        else
        {
          if (previousTag == null)
          {
        	  classSince = Version.valueOf(since);
          }
          else
          {
        	  previousTag.setSince(Version.valueOf(since));
          }
          break;
        }
      }
    }
	return classSince;
  }

  private List<Signature> parseSignatures(String content) 
  {
	List<Signature> signatures = new ArrayList<>();
    Matcher matcher = MEMBER_SIGNATURE_PATTERN.matcher(content);
    while (matcher.find())
    {
    	String signature = toSignature(matcher.group(1));
    	signatures.add(new Signature(signature, matcher.start()));
    }
    Comparator<Signature> c = Comparator.comparing(tag -> tag.startPos);
    Collections.sort(signatures, c);
	return signatures;
  }

  private String toSignature(String signature)
  {
	signature = signature.replace("\n", "");
    signature = signature.replace("\r", "");
	signature = TYPE_LINK_PATTERN.matcher(signature).replaceAll("$2.");
	signature = HTML_TAGS_PATTERN.matcher(signature).replaceAll("");
	signature = ANNOTATION_PATTERNS.matcher(signature).replaceAll("");
	signature = signature.replace("&lt;", "<");
	signature = signature.replace("&gt;", ">");
	signature = signature.replace("&nbsp;", " ");
	signature = signature.replace("&#8203;", "");
	while (StringUtils.countMatches(signature, ")") > 1)
	{
	  signature = StringUtils.substringAfter(signature, ")");
	}
	return signature;
  }

  private List<ApiMethod> toMethods(List<Signature> signatures)
  {
    return signatures
            .stream()
            .filter(this::isMethod)
            .map(this::parseMethod)
            .collect(Collectors.toList());
  }

  private List<ApiConstructor> toConstructors(List<Signature> signatures)
  {
    return signatures
            .stream()
            .filter(this::isConstructor)
            .map(this::parseConstructor)
            .collect(Collectors.toList());
  }

  private List<ApiField> toFields(List<Signature> signatures)
  {
    return signatures
            .stream()
            .filter(this::isField)
            .map(this::parseField)
            .collect(Collectors.toList());
  }

  private boolean isMethod(Signature signature)
  {
	return !isField(signature) && !isConstructor(signature);
  }

  private boolean isConstructor(Signature signature)
  {
    return !isField(signature) && StringUtils.contains(signature.signature, getSimpleClassName()+"(");
  }
  
  private String getSimpleClassName() 
  {
	return StringUtils.substringAfterLast(getClassName(), ".");
  }

  private boolean isField(Signature signature)
  {
	  return !StringUtils.contains(signature.signature, '(');
  }

  private ApiMethod parseMethod(Signature sig)
  {
    String signature = sig.signature;
    String start = StringUtils.substringBefore(signature, "(");
    String[] parts = start.split(" ");    	
    String name = parts[parts.length-1];
    String returnType = parts[parts.length-2];
    List<ApiModifier> modifiers = parseModifiers(parts);
    String args = StringUtils.substringBetween(signature, "(", ")");
    List<ApiArgument> arguments = parseArguments(args);
    return new ApiMethod(name, returnType, arguments, modifiers, sig.since);
  }
  
  private ApiConstructor parseConstructor(Signature sig)
  {
    String signature = sig.signature;
    String start = StringUtils.substringBefore(signature, "(");
    List<ApiModifier> modifiers = parseModifiers(start);
    String args = StringUtils.substringBetween(signature, "(", ")");
    List<ApiArgument> argumentTypes = parseArguments(args);
    return new ApiConstructor(argumentTypes, modifiers, sig.since);
  }
  
  private ApiField parseField(Signature sig)
  {
    String signature = sig.signature;
    String[] parts = signature.split(" ");
    if (parts.length < 2)
    {
    	System.out.println("");
    }

    String name = parts[parts.length-1];
    String type = parts[parts.length-2];
    List<ApiModifier> modifiers = parseModifiers(parts);
    return new ApiField(name, type, modifiers, sig.since);
  }
  
  private List<ApiModifier> parseModifiers(String start) 
  {
	return parseModifiers(start.split(" "));
  }

  private List<ApiModifier> parseModifiers(String[] modifiers) 
  {
	return Arrays
        .stream(modifiers)
        .flatMap(modifier -> ApiModifier.parse(modifier).stream())
        .collect(Collectors.toList());
  }

  private List<ApiArgument> parseArguments(String args)
  {
    if (StringUtils.isBlank(args))
    {
      return Collections.emptyList();
    }
    return Arrays
        .stream(args.split(","))
        .map(this::parseArgument)
        .collect(Collectors.toList());
  }
  
  private ApiArgument parseArgument(String arg)
  {
	  String name = StringUtils.substringAfterLast(arg, " ");
	  String type = StringUtils.substringBeforeLast(arg, " ");
	  return new ApiArgument(name, type);
  }

  private static final class Signature
  {
    private final String signature;
    private final int startPos;
    private Version since = Version.UNDEFINED;

    public Signature(String signature, int startPos)
    {
      this.signature = signature;
      this.startPos = startPos;
    }

    public void setSince(Version since)
    {
      this.since = since;
    }
    
    @Override
    public String toString()
    {
      return "Signature [signature="+signature+",pos="+startPos+" since= "+since+"]";
    }
  }

}
