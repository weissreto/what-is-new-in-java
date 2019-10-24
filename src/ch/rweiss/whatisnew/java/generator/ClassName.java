package ch.rweiss.whatisnew.java.generator;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.lang3.StringUtils;

class ClassName
{
  private String apiClassName;

  ClassName(String apiClassName)
  {
    this.apiClassName = apiClassName;
  }
  
  String getApiSimpleName()
  {
    return StringUtils.substringAfterLast(apiClassName, ".");
  }
  
  String getApiPackageName()
  {
    return StringUtils.substringBeforeLast(apiClassName, ".");
  }
  
  public String getApiFullQualifiedName()
  {
    return apiClassName;
  }
  
  String getGeneratorSimpleName()
  {
    return "WhatIsNewIn"+getApiSimpleName();
  }
  
  String getGeneratorPackageName()
  {
    return "whatisnewin."+getApiPackageName();
  }
  
  String getGeneratorFullQualifiedName()
  {
    return getGeneratorPackageName()+"."+getGeneratorSimpleName();
  }
  
  Path getGeneratorJavaFile(Path outputPath)
  {
    String fullClassName = getGeneratorFullQualifiedName();
    String fileName = StringUtils.replace(fullClassName, ".", FileSystems.getDefault().getSeparator());
    Path relativeFilePath = Paths.get(fileName+".java");
    
    Path generatorJavaFile = outputPath.resolve(relativeFilePath);
    return generatorJavaFile;
  }
}
