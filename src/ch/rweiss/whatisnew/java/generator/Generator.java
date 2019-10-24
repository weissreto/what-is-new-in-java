package ch.rweiss.whatisnew.java.generator;

import java.io.BufferedWriter;
import java.io.IOException;
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
      Path outputFile = name.getGeneratorJavaFile(outputPath);
      Files.createDirectories(outputFile.getParent());
      try (BufferedWriter writer = Files.newBufferedWriter(outputFile))
      {
        new ClassGenerator(name, apiClass, writer).generate();
      }
    }
    catch(WhatIsNewInException | IOException ex)
    {
      throw new WhatIsNewInException("Could not generate java class for "+name.getApiFullQualifiedName(), ex);
    }
  }
}
