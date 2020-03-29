package ch.rweiss.whatisnew.java.apidoc.parser;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import ch.rweiss.whatisnew.java.apidoc.model.ApiClass;
import ch.rweiss.whatisnew.java.apidoc.model.ApiDoc;
import ch.rweiss.whatisnew.java.apidoc.model.Version;

public class ApiDocParser extends SimpleFileVisitor<Path>
{
  private final Path root;
  private final SortedSet<ApiClass> classes = new TreeSet<>();
  private final Map<String, ApiClass> classNames = new HashMap<>();

  private ApiDocParser(Path root)
  {
    this.root = root;
  }

  @Override
  public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException
  {
    if (dir.getFileName().toString().indexOf('-') >= 0)
    {
      return FileVisitResult.SKIP_SUBTREE;
    }
    else
    {
      return FileVisitResult.CONTINUE;
    }
  }

  @Override
  public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException
  {
    Path relativePath = root.relativize(file);
    if (relativePath.getNameCount() <= 1)
    {
      return FileVisitResult.CONTINUE;
    }
    String fileName = file.getFileName().toString();
    if (fileName.endsWith(".html") &&
        fileName.toString().indexOf("-") < 0)
    {
      relativePath = relativePath.subpath(1, relativePath.getNameCount());
      ApiClass clazz = new ClassFileParser(file, relativePath).parse();
      classes.add(clazz);
      classNames.put(clazz.getName(), clazz);
    }
    return FileVisitResult.CONTINUE;
  }

  private ApiDoc getApiDoc()
  {
    List<ApiClass> undefinedInnerClassesWithDefinedOuter = classes
        .stream()
        .filter(ApiDocParser::isInnerClass)
        .filter(cl -> Version.UNDEFINED.equals(cl.getSince()))
        .filter(this::hasDefinedOuterClass)
        .collect(Collectors.toList());
    classes.removeAll(undefinedInnerClassesWithDefinedOuter);
    List<ApiClass> definedInnerClasses = undefinedInnerClassesWithDefinedOuter
        .stream()
        .map(this::convertToClassWithSinceOfOuterClass)
        .collect(Collectors.toList());
    classes.addAll(definedInnerClasses);
    return new ApiDoc(classes);
  }
  
  private static boolean isInnerClass(ApiClass cl)
  {
    String outerClassName = getOuterClassName(cl);
    outerClassName = StringUtils.substringAfterLast(outerClassName, ".");
    return Character.isUpperCase(outerClassName.charAt(0));
  }
  
  private boolean hasDefinedOuterClass(ApiClass cl)
  {
    String outerClassName = getOuterClassName(cl);
    ApiClass outer = classNames.get(outerClassName);
    if (outer == null)
    {
      return false;      
    }
    if (outer.getSince().equals(Version.UNDEFINED))
    {
      return false;
    }
    return true;
  }
  
  private ApiClass convertToClassWithSinceOfOuterClass(ApiClass original)
  {
    String outerClassName = getOuterClassName(original);
    ApiClass outer = classNames.get(outerClassName);
    return new ApiClass(
        original.getName(), 
        original.getConstructors(), 
        original.getMethods(), 
        original.getFields(), 
        outer.getSince());
  }

  private static String getOuterClassName(ApiClass cl)
  {
    String className = cl.getName();
    return StringUtils.substringBeforeLast(className, ".");
  }

  public static ApiDoc parse(Path root) throws IOException
  {
    Path apiRoot = root.resolve("api");
    ApiDocParser visitor = new ApiDocParser(apiRoot);
    Files.walkFileTree(apiRoot, visitor);
    return visitor.getApiDoc();
  }
}
