package ch.rweiss.whatisnew.java.resolver;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import ch.rweiss.whatisnew.java.WhatIsNewInException;
import ch.rweiss.whatisnew.java.apidoc.model.ApiMethod;
import ch.rweiss.whatisnew.java.generator.model.JavaMethod;

class JavaMethodResolver
{
  private final Class<?> javaClass;
  private final ApiMethod api;

  public JavaMethodResolver(Class<?> javaClass, ApiMethod api)
  {
    this.javaClass = javaClass;
    this.api = api;
  }

  public JavaMethod resolve()
  {
    Method java = toJava();
    if (java == null)
    {
      System.err.println("Method "+api.getName()+" not found for class "+javaClass.getName());
      return null;
    }
    if (!Modifier.isPublic(java.getModifiers()))
    {
      return null;
    }
    if (isObjectDeclaredMethod(java))
    {
      return null;
    }
    return new JavaMethod(api, java);
  }

  private boolean isObjectDeclaredMethod(Method java)
  {
    try
    {
      Object.class.getDeclaredMethod(java.getName(), java.getParameterTypes());
      return true;
    }
    catch(NoSuchMethodException ex)
    {
      return false;
    }
  }

  private Method toJava()
  {
    Method method = findWithParamTypes();
    if (method != null)
    {
      return method;
    }
    method = findWithTypeVariableParams();
    if (method != null)
    {
      return method;
    }
    return null;
  }

  private Method findWithParamTypes()
  {
    try
    {
      Class<?>[] parameterTypes = api
              .getRawArgumentTypes()
              .stream()
              .map(JavaClassResolver::resolve)
              .toArray(Class[]::new);
      return javaClass.getDeclaredMethod(api.getName(), parameterTypes);
    }
    catch(WhatIsNewInException | NoSuchMethodException | SecurityException ex)
    {
      return null;
    }
  }

  private Method findWithTypeVariableParams()
  {
    return Arrays
            .stream(javaClass.getDeclaredMethods())
            .filter(method -> matches(method))
            .findAny()
            .orElse(null);
  }

  private boolean matches(Method method)
  {
    return method.getName().equals(api.getName())&&
           method.getParameterCount() == api.getArgumentTypes().size() &&
           matches(method.getParameters(), api.getArgumentTypes());
  }

  private boolean matches(Parameter[] parameters, List<String> argumentTypes)
  {
    for (int pos = 0; pos < parameters.length; pos++)
    {
      Parameter parameter = parameters[pos];
      String argumentType = argumentTypes.get(pos);
      if (argumentType.endsWith("..."))
      {
        argumentType = StringUtils.removeEnd(argumentType, "...") + "[]";
      }
      String parameterType = new TypeNameConverter(parameter.getParameterizedType()).convertToString();
      if (!parameterType.equals(argumentType))
      {
        return false;
      }
    }
    return true;
  }
}