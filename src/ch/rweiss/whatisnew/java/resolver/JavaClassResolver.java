package ch.rweiss.whatisnew.java.resolver;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import ch.rweiss.whatisnew.java.model.Version;

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
    if (!isPublicAccessible(java))
    {
      return null;
    }
    List<JavaConstructor> constructors = toJavaConstructors(java, api.getConstructors());
    constructors = removeDuplicateConstructors(constructors);
    List<JavaMethod> methods = toJavaMethods(java, api.getMethods());
    methods = removeDuplicateMethods(methods);
    return new JavaClass(api, java, constructors, methods);
  }
  
  private boolean isPublicAccessible(Class<?> clazz)
  {
    if (clazz.getEnclosingClass() != null)
    {
      if (!isPublicAccessible(clazz.getEnclosingClass()))
      {
        return false;
      }
    }
    return Modifier.isPublic(clazz.getModifiers());
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

  private List<JavaMethod> removeDuplicateMethods(List<JavaMethod> methods)
  {
    Map<Method, JavaMethod> uniqueSet = new HashMap<>(methods.size());
    for (JavaMethod method : methods)
    {
      JavaMethod first = uniqueSet.get(method.getJava());
      if (first != null)
      {
        if (Version.UNDEFINED.equals(first.getSince()) &&
            !Version.UNDEFINED.equals(method.getSince()))
        {
          uniqueSet.put(method.getJava(), method);
        }
      }
      else
      {
        uniqueSet.put(method.getJava(), method);
      }
    }
    return new ArrayList<>(uniqueSet.values());
  }

  private List<JavaConstructor> removeDuplicateConstructors(List<JavaConstructor> constructors)
  {
    Map<Constructor<?>, JavaConstructor> uniqueSet = new HashMap<>(constructors.size());
    for (JavaConstructor constructor : constructors)
    {
      JavaConstructor first = uniqueSet.get(constructor.getJava());
      if (first != null)
      {
        if (Version.UNDEFINED.equals(first.getSince()) &&
            !Version.UNDEFINED.equals(constructor.getSince()))
        {
          uniqueSet.put(constructor.getJava(), constructor);
        }
      }
      else
      {
        uniqueSet.put(constructor.getJava(), constructor);
      }
    }
    return new ArrayList<>(uniqueSet.values());
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
