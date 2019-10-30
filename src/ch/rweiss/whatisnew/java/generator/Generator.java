package ch.rweiss.whatisnew.java.generator;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import ch.rweiss.whatisnew.java.WhatIsNewInException;
import ch.rweiss.whatisnew.java.model.ApiClass;
import ch.rweiss.whatisnew.java.model.ApiDoc;
import ch.rweiss.whatisnew.java.model.Version;

public class Generator
{
  private final ApiDoc apiDoc;
  private final Path outputPath;
  private final List<Version> versions;

  public Generator(ApiDoc apiDoc, List<Version> versions, Path outputPath)
  {
    this.apiDoc = apiDoc;
    this.versions = versions;
    this.outputPath = outputPath;
  }

  public void generate()
  {
    apiDoc.getClasses().forEach(this::generate);
  }
  
  private void generate(ApiClass apiClass) 
  {
    ClassName name = new ClassName(apiClass.getName());
    try
    {
      if (!isPublic(name))
      {
        return;
      }
        
      Path outputFile = name.getGeneratorJavaFile(outputPath);
      Files.createDirectories(outputFile.getParent());
      try (Printer printer = new Printer(outputFile))
      {
        new ClassGenerator(name, apiClass, versions, printer).generate();
      }
    }
    catch(WhatIsNewInException | IOException ex)
    {
      System.err.println("Could not generate java class for "+name.getApiFullQualifiedName()+" ("+ex.getMessage()+")");
    }
  }

  private boolean isPublic(ClassName name)
  {
    Class<?> clazz = ClassGenerator.getJavaClass(name);
    return Modifier.isPublic(clazz.getModifiers());
  }
}
