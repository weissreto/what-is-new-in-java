package ch.rweiss.whatisnew.java.generator;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;

import ch.rweiss.whatisnew.java.WhatIsNewInException;
import ch.rweiss.whatisnew.java.model.ApiClass;
import ch.rweiss.whatisnew.java.model.ApiDoc;

public class Generator
{
  private final ApiDoc apiDoc;
  private final Path outputPath;

  public Generator(ApiDoc apiDoc, Path outputPath)
  {
    this.apiDoc = apiDoc;
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
        new ClassGenerator(name, apiClass, printer).generate();
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
