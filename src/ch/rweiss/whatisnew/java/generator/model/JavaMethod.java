package ch.rweiss.whatisnew.java.generator.model;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.stream.Collectors;

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

  public boolean isStatic()
  {
    return Modifier.isStatic(java.getModifiers());
  }
  
  @Override
  public String toString()
  {
    return "JavaMethod ["+
        getName()+"("+
        Arrays
            .stream(getParameterTypes())
            .map(Class::getCanonicalName)
            .collect(Collectors.joining(", "))+
    ")]";
  }
}
