package ch.rweiss.whatisnew.java.generator.model;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import ch.rweiss.whatisnew.java.model.ApiArgument;
import ch.rweiss.whatisnew.java.model.ApiField;
import ch.rweiss.whatisnew.java.model.ApiMethod;
import ch.rweiss.whatisnew.java.model.Version;

public class JavaMethod
{
  private final Version since;
  private final Method java;
  private final List<ApiArgument> apiArguments;
  
  public JavaMethod(ApiMethod api, Method java)
  {
    this.since = api.getSince();
    this.apiArguments = api.getArguments();
    this.java = java;
  }
  
  public JavaMethod(ApiField api, Method java)
  {
    this.since = api.getSince();
    this.apiArguments = Collections.emptyList();
    this.java = java;
  }

  public Method getJava()
  {
    return java;
  }
    
  public Version getSince()
  {
    return since;
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
      JavaParameter param = new JavaParameter(apiArguments.get(pos), java.getParameters()[pos]);	
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
