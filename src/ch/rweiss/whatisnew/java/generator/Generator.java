package ch.rweiss.whatisnew.java.generator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import ch.rweiss.whatisnew.java.WhatIsNewInException;
import ch.rweiss.whatisnew.java.apidoc.model.Version;
import ch.rweiss.whatisnew.java.generator.model.JavaApi;
import ch.rweiss.whatisnew.java.generator.model.JavaClass;

public class Generator
{
  private final JavaApi api;
  private final Path outputPath;
  private final List<Version> versions;
  private final Path sourcePath;
  private static final Path RELATIVE_SOURCE_PATH = Paths.get("src", "main", "java");

  public Generator(JavaApi api, List<Version> versions, Path outputPath)
  {
    this.api = api;
    this.versions = versions;
    this.outputPath = outputPath;
    this.sourcePath = outputPath.resolve(RELATIVE_SOURCE_PATH);
  }

  public void generate()
  {
    generateMavenProject();
    api.getClasses().forEach(this::generate);
  }
  
  private void generateMavenProject()
  {
    try
    {
      Files.createDirectories(outputPath);
      Path pomFile = outputPath.resolve("pom.xml");
      try (Printer printer = new Printer(pomFile))
      {
        new MavenProjectGenerator(versions, printer).generate();
      }
    }
    catch(WhatIsNewInException | IOException ex)
    {
      System.err.println("Could not generate pom.xml ("+ex.getMessage()+")");
    }
  }

  private void generate(JavaClass clazz) 
  {
    try
    {
      Path outputFile = clazz.getGeneratorJavaFile(sourcePath);
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
