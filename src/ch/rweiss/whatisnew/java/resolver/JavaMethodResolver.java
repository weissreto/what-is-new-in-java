package ch.rweiss.whatisnew.java.resolver;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import ch.rweiss.whatisnew.java.WhatIsNewInException;
import ch.rweiss.whatisnew.java.generator.TypeUtil;
import ch.rweiss.whatisnew.java.generator.model.JavaMethod;
import ch.rweiss.whatisnew.java.model.ApiMethod;

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
              .getArgumentTypes()
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
    return method.getParameterCount() == api.getArgumentTypes().size() &&
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
      String parameterType = TypeUtil.toRawName(parameter.getType());
      Type type = parameter.getParameterizedType();
      if (type instanceof TypeVariable)
      {
        parameterType = ((TypeVariable<?>) type).getName();
      }
      else if (type instanceof GenericArrayType)
      {
        parameterType = ((GenericArrayType) type).getGenericComponentType().getTypeName()+"[]";
      }
      if (!parameterType.equals(argumentType))
      {
        return false;
      }
    }
    return true;
  }
}