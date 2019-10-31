package ch.rweiss.whatisnew.java.resolver;

import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;

import ch.rweiss.whatisnew.java.WhatIsNewInException;
import ch.rweiss.whatisnew.java.generator.model.JavaClass;
import ch.rweiss.whatisnew.java.generator.model.JavaConstructor;
import ch.rweiss.whatisnew.java.generator.model.JavaMethod;
import ch.rweiss.whatisnew.java.model.ApiClass;
import ch.rweiss.whatisnew.java.model.ApiConstructor;
import ch.rweiss.whatisnew.java.model.ApiMethod;

class JavaClassResolver
{
  private ApiClass api;

  JavaClassResolver(ApiClass api)
  {
    this.api = api;
  }

  JavaClass resolve() 
  {
    Class<?> java = toJava();
    if (!Modifier.isPublic(java.getModifiers()))
    {
      return null;
    }
    List<JavaConstructor> constructors = toJavaConstructors(java, api.getConstructors());
    List<JavaMethod> methods = toJavaMethods(java, api.getMethods());
    return new JavaClass(api, java, constructors, methods);
  }
  
  private List<JavaConstructor> toJavaConstructors(Class<?> javaClass, List<ApiConstructor> constructors)
  {
    return constructors
        .stream()
        .map(constructor -> toJava(javaClass, constructor))
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
  }
  
  private JavaConstructor toJava(Class<?> javaClass, ApiConstructor constructor)
  {
    return new JavaConstructorResolver(javaClass, constructor).resolve();
  }
  
  private List<JavaMethod> toJavaMethods(Class<?> javaClass, List<ApiMethod> methods)
  {
    return methods
        .stream()
        .map(method -> toJava(javaClass, method))
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
  }

  private JavaMethod toJava(Class<?> javaClass, ApiMethod method)
  {
    return new JavaMethodResolver(javaClass, method).resolve();
  }

  private Class<?> toJava() 
  {
    return resolve(api.getName());
  }
  
  static Class<?> resolve(String fullQualifiedName) 
  {
    try
    {
      if (fullQualifiedName.endsWith("..."))
      {
        fullQualifiedName = StringUtils.removeEnd(fullQualifiedName, "...") + "[]";
      }
      return ClassUtils.getClass(fullQualifiedName);
    }
    catch(ClassNotFoundException ex)
    {
      throw new WhatIsNewInException(ex);
    }    
  }

}
