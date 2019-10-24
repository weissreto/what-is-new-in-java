package ch.rweiss.whatisnew.java;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

public class Directory
{

  public static void delete(Path outputPath) throws IOException
  {
    if (Files.exists(outputPath))
    {
      Files.walkFileTree(outputPath, new DeleteVisitor());
    }
  }
  
  private static final class DeleteVisitor extends SimpleFileVisitor<Path>
  {
    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException
    {
      Files.delete(file);
      return FileVisitResult.CONTINUE;
    }
    
    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException
    {
      Files.delete(dir);
      return FileVisitResult.CONTINUE;
    }
  }

}
