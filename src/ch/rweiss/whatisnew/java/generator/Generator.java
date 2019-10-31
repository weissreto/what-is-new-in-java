package ch.rweiss.whatisnew.java.generator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import ch.rweiss.whatisnew.java.WhatIsNewInException;
import ch.rweiss.whatisnew.java.generator.model.JavaApi;
import ch.rweiss.whatisnew.java.generator.model.JavaClass;
import ch.rweiss.whatisnew.java.model.Version;

public class Generator
{
  private final JavaApi api;
  private final Path outputPath;
  private final List<Version> versions;

  public Generator(JavaApi api, List<Version> versions, Path outputPath)
  {
    this.api = api;
    this.versions = versions;
    this.outputPath = outputPath;
  }

  public void generate()
  {
    api.getClasses().forEach(this::generate);
  }
  
  private void generate(JavaClass clazz) 
  {
    try
    {
      Path outputFile = clazz.getGeneratorJavaFile(outputPath);
      Files.createDirectories(outputFile.getParent());
      try (Printer printer = new Printer(outputFile))
      {
        new ClassGenerator(clazz, versions, printer).generate();
      }
    }
    catch(WhatIsNewInException | IOException ex)
    {
      System.err.println("Could not generate java class for "+clazz.getFullQualifiedName()+" ("+ex.getMessage()+")");
    }
  }
}
