package ch.rweiss.whatisnew.java.resolver;

import java.lang.reflect.Constructor;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import ch.rweiss.whatisnew.java.WhatIsNewInException;
import ch.rweiss.whatisnew.java.generator.RawTypeNameGenerator;
import ch.rweiss.whatisnew.java.generator.model.JavaConstructor;
import ch.rweiss.whatisnew.java.model.ApiConstructor;

class JavaConstructorResolver
{
  private final Class<?> javaClass;
  private final ApiConstructor api;

  public JavaConstructorResolver(Class<?> javaClass, ApiConstructor api)
  {
    this.javaClass = javaClass;
    this.api = api;
  }

  public JavaConstructor resolve()
  {
    Constructor<?> java = toJava();
    if (java == null)
    {
      System.err.println("Constructor not found for class "+javaClass.getName());
      return null;      
    }
    if (!Modifier.isPublic(java.getModifiers()))
    {
      return null;
    }
    return new JavaConstructor(api, java);
  }

  private Constructor<?> toJava()
  {
    Constructor<?> constructor = findWithParamTypes();
    if (constructor != null)
    {
      return constructor;
    }
    constructor = findWithTypeVariableParams();
    if (constructor != null)
    {
      return constructor;
    }
    return null;
  }

  private Constructor<?> findWithParamTypes()
  {
    try
    {
      Class<?>[] parameterTypes = api
              .getArgumentTypes()
              .stream()
              .map(JavaClassResolver::resolve)
              .toArray(Class[]::new);
      return javaClass.getDeclaredConstructor(parameterTypes);
    }
    catch(WhatIsNewInException | NoSuchMethodException | SecurityException ex)
    {
      return null;
    }
  }

  private Constructor<?> findWithTypeVariableParams()
  {
    return Arrays
            .stream(javaClass.getDeclaredConstructors())
            .filter(method -> matches(method))
            .findAny()
            .orElse(null);
  }

  private boolean matches(Constructor<?> constructor)
  {
    return constructor.getParameterCount() == api.getArgumentTypes().size() &&
           matches(constructor.getParameters(), api.getArgumentTypes());
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
      String parameterType = RawTypeNameGenerator.toRawName(parameter.getType());
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