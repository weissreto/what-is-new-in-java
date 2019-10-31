package ch.rweiss.whatisnew.java.generator.model;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

import ch.rweiss.whatisnew.java.model.ApiMethod;
import ch.rweiss.whatisnew.java.model.Version;

public class JavaMethod
{
  private final ApiMethod api;
  private final Method java;
  
  public JavaMethod(ApiMethod api, Method java)
  {
    this.api = api;
    this.java = java;
  }
  
  public Method getJava()
  {
    return java;
  }
    
  public Version getSince()
  {
    return api.getSince();
  }

  public String getName()
  {
    return java.getName();
  }

  public Class<?> getReturnType()
  {
    return java.getReturnType();
  }

  public Type getGenericReturnType()
  {
    return java.getGenericReturnType();
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

  public boolean isStatic()
  {
    return Modifier.isStatic(java.getModifiers());
  }
}