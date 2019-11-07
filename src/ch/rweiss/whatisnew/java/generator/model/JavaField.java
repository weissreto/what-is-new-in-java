package ch.rweiss.whatisnew.java.generator.model;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

import ch.rweiss.whatisnew.java.apidoc.model.ApiField;
import ch.rweiss.whatisnew.java.apidoc.model.Version;

public class JavaField
{
  private final ApiField api;
  private final Field java;

  public JavaField(ApiField api, Field java)
  {
    this.api = api;
    this.java = java;
  }

  public String getName()
  {
    return java.getName();
  }

  public Version getSince()
  {
    return api.getSince();
  }

  public Type getGenericReturnType()
  {
    return java.getGenericType();
  }
  
  @Override
  public String toString()
  {
    return "JavaField ["+getName()+"]";
  }
}
