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

  public JavaParameter[] getParameters()
  {
	JavaParameter[] parameters = new JavaParameter[java.getParameterCount()];
	for (int pos = 0; pos < java.getParameterCount(); pos++)
	{
      JavaParameter param = new JavaParameter(api.getArguments().get(pos), java.getParameters()[pos]);	
	  parameters[pos] = param;
	}
    return parameters;
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
