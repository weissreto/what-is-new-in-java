package ch.rweiss.whatisnew.java.parser;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import ch.rweiss.whatisnew.java.WhatIsNewInException;
import ch.rweiss.whatisnew.java.model.ApiClass;
import ch.rweiss.whatisnew.java.model.ApiMethod;
import ch.rweiss.whatisnew.java.model.Version;

public class ClassFileParser
{
  private static final Pattern SINCE_PATTERN = Pattern
          .compile("<dt><span class=\"simpleTagLabel\">Since:<\\/span><\\/dt>[\n\r]+<dd>([0-9\\.]+)<\\/dd>", Pattern.MULTILINE);
  private static final Pattern TAG_PATTERN = Pattern.compile("<a id=\"([^\"]*)\">");
  private Path file;
  private Path relativePath;

  public ClassFileParser(Path file, Path relativePath)
  {
    this.file = file;
    this.relativePath = relativePath;
  }

  public ApiClass parse() throws IOException
  {
    List<Tag> tags = new ArrayList<>();
    String content = Files.readString(file);
    Matcher matcher = TAG_PATTERN.matcher(content);
    while (matcher.find())
    {
      tags.add(new Tag(matcher.group(1), matcher.start()));
    }
    Comparator<Tag> c = Comparator.comparing(tag -> tag.startPos);
    Collections.sort(tags, c);
    matcher = SINCE_PATTERN.matcher(content);
    while (matcher.find())
    {
      int sinceStart = matcher.start();
      String since = matcher.group(1);
      Tag previousTag = null;
      for (Tag tag : tags)
      {
        if (tag.startPos < sinceStart)
        {
          previousTag = tag;
        }
        else
        {
          if (previousTag == null)
          {
            throw new WhatIsNewInException("Found @since but no tag");
          }
          previousTag.setSince(Version.valueOf(since));
          break;
        }
      }
    }

    List<ApiMethod> methods = toMethods(tags);    
    
    String relativePathStr = relativePath.toString();
    relativePathStr = StringUtils.removeEnd(relativePathStr, ".html");
    String className = StringUtils.replace(relativePathStr, FileSystems.getDefault().getSeparator(), ".");
    return new ApiClass(className, Collections.emptyList(), methods,
            Collections.emptyList(), toClassSince(tags));
  }

  private Version toClassSince(List<Tag> tags)
  {
    return tags
        .stream()
        .takeWhile(tag -> !tag.tag.contains(".summary"))
        .filter(tag -> !Version.UNDEFINED.equals(tag.since))
        .map(tag -> tag.since)
        .findAny()
        .orElse(Version.UNDEFINED);
  }

  private List<ApiMethod> toMethods(List<Tag> tags)
  {
    return tags
            .stream()
            .filter(this::isMethod)
            .map(this::parseMethod)
            .collect(Collectors.toList());
  }

  private boolean isMethod(Tag tag)
  {
    String signature = tag.tag;
    return !StringUtils.startsWith(signature, "&lt;init&gt;") &&
            StringUtils.contains(signature, '(') &&
            StringUtils.contains(signature, ')');
  }

  private ApiMethod parseMethod(Tag tag)
  {
    String signature = tag.tag;
    String name = StringUtils.substringBefore(signature, "(");
    String args = StringUtils.substringBetween(signature, "(", ")");
    List<String> argumentTypes = parseArgumentTypes(args);
    return new ApiMethod(name, argumentTypes, tag.since);
  }

  private List<String> parseArgumentTypes(String args)
  {
    if (args.isBlank())
    {
      return Collections.emptyList();
    }
    return List.of(args.split(","));
  }

  private static final class Tag
  {
    private final String tag;
    private final int startPos;
    private Version since = Version.UNDEFINED;

    public Tag(String tag, int startPos)
    {
      this.tag = tag;
      this.startPos = startPos;
    }

    public void setSince(Version since)
    {
      this.since = since;
    }
    
    @Override
    public String toString()
    {
      return "Tag [tag="+tag+",pos="+startPos+" since= "+since+"]";
    }
  }

}
