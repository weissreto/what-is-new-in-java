package ch.rweiss.whatisnew.java.generator.model;

import java.util.List;

public class JavaApi
{
  private final List<JavaClass> classes;
  
  public JavaApi(List<JavaClass> classes)
  {
    this.classes = classes;
  }
  
  public List<JavaClass> getClasses()
  {
    return classes;
  }
}
