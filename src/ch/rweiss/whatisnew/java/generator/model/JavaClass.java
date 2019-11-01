package ch.rweiss.whatisnew.java.generator.model;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import ch.rweiss.whatisnew.java.model.ApiClass;
import ch.rweiss.whatisnew.java.model.Version;

public class JavaClass
{
  private final ApiClass api;
  private final Class<?> java;
  private final List<JavaConstructor> constructors;
  private final List<JavaMethod> methods;
  
  public JavaClass(ApiClass api, Class<?> java, List<JavaConstructor> constructors,
          List<JavaMethod> methods)
  {
    this.api = api;
    this.java = java;
    this.constructors = constructors;
    this.methods = methods;
  }
  
  public Class<?> getJava()
  {
    return java;
  }
  
  public String getSimpleName()
  {
    return java.getSimpleName();
  }
  
  public String getPackageName()
  {
    return java.getPackageName();
  }
  
  public String getFullQualifiedName()
  {
    return java.getCanonicalName();
  }
  
  public String getGeneratorSimpleName()
  {
    // special handling for inner classes Map.Entry will be Map$Entry
    String qualifiedSimpleName = StringUtils.removeStart(java.getName(), java.getPackageName()+".");
    return "WhatIsNewIn"+qualifiedSimpleName;
  }
  
  public String getGeneratorPackageName()
  {
    return "whatisnewin."+getPackageName();
  }
  
  public String getGeneratorFullQualifiedName()
  {
    return getGeneratorPackageName()+"."+getGeneratorSimpleName();
  }
  
  public Path getGeneratorJavaFile(Path outputPath)
  {
    String fullClassName = getGeneratorFullQualifiedName();
    String fileName = StringUtils.replace(fullClassName, ".", FileSystems.getDefault().getSeparator());
    Path relativeFilePath = Paths.get(fileName+".java");
    
    Path generatorJavaFile = outputPath.resolve(relativeFilePath);
    return generatorJavaFile;
  }

  public List<JavaMethod> getMethods()
  {
    return methods;
  }

  public List<JavaConstructor> getConstructors()
  {
    return constructors;
  }

  public Version getSince()
  {
    return api.getSince();
  }
  
  @Override
  public String toString()
  {
    return "JavaClass [name="+java.getCanonicalName()+"]"; 
  }
}
