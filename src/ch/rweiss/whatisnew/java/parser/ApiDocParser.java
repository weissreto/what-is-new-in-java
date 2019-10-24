package ch.rweiss.whatisnew.java.parser;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

import ch.rweiss.whatisnew.java.model.ApiClass;
import ch.rweiss.whatisnew.java.model.ApiDoc;

public class ApiDocParser extends SimpleFileVisitor<Path>
{
  private final Path root;
  private final List<ApiClass> classes = new ArrayList<>();

  private ApiDocParser(Path root)
  {
    this.root = root;
  }

  public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException
  {
    Path relativize = root.relativize(dir);
    if (dir.getFileName().toString().indexOf('-') >= 0)
    {
      return FileVisitResult.SKIP_SUBTREE;
    }
    else
    {
      return FileVisitResult.CONTINUE;
    }
  }

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
    }
    return FileVisitResult.CONTINUE;
  }

  private ApiDoc getApiDoc()
  {
    return new ApiDoc(classes);
  }

  public static ApiDoc parse(Path root) throws IOException
  {
    Path apiRoot = root.resolve("api");
    ApiDocParser visitor = new ApiDocParser(apiRoot);
    Files.walkFileTree(apiRoot, visitor);
    return visitor.getApiDoc();
  }
}
