package ch.rweiss.whatisnew.java.generator.model;

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.stream.Collectors;

import ch.rweiss.whatisnew.java.model.ApiConstructor;
import ch.rweiss.whatisnew.java.model.Version;

public class JavaConstructor
{
  private final ApiConstructor api;
  private final Constructor<?> java;
  
  public JavaConstructor(ApiConstructor api, Constructor<?> java)
  {
    this.api = api;
    this.java = java;
  }

  public Constructor<?> getJava()
  {
    return java;
  }
  
  public Version getSince()
  {
    return api.getSince();
  }

  public Parameter[] getParameters()
  {
    return java.getParameters();
  }

  public Class<?>[] getParameterTypes()
  {
    return java.getParameterTypes();
  }

  public TypeVariable<?>[] getTypeParameters()
  {
    return java.getTypeParameters();
  }

  public Class<?>[] getExceptionTypes()
  {
    return java.getExceptionTypes();
  }
  
  @Override
  public String toString()
  {
    return "JavaConstructor [("+
        Arrays
            .stream(getParameterTypes())
            .map(Class::getCanonicalName)
            .collect(Collectors.joining(", "))+
    ")]";
  }
}
