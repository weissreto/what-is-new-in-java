package ch.rweiss.whatisnew.java.resolver;

import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;

import ch.rweiss.whatisnew.java.WhatIsNewInException;
import ch.rweiss.whatisnew.java.apidoc.model.ApiClass;
import ch.rweiss.whatisnew.java.apidoc.model.ApiConstructor;
import ch.rweiss.whatisnew.java.apidoc.model.ApiField;
import ch.rweiss.whatisnew.java.apidoc.model.ApiMethod;
import ch.rweiss.whatisnew.java.apidoc.model.ApiModifier;
import ch.rweiss.whatisnew.java.generator.model.JavaClass;
import ch.rweiss.whatisnew.java.generator.model.JavaConstructor;
import ch.rweiss.whatisnew.java.generator.model.JavaField;
import ch.rweiss.whatisnew.java.generator.model.JavaMethod;

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

    if (java.isAnnotation())
    {
      return createClassForAnnotation(java);
    }
    else
    {
      return createClass(java);
    }
  }

  private JavaClass createClass(Class<?> java)
  {
    List<JavaField> fields = toJavaFields(java, api.getFields());
    List<JavaConstructor> constructors = toJavaConstructors(java, api.getConstructors());
    List<JavaMethod> methods = toJavaMethods(java, api.getMethods());
    return new JavaClass(api, java, fields, constructors, methods);
  }

  private JavaClass createClassForAnnotation(Class<?> java)
  {
    List<ApiField> constants = api
        .getFields()
        .stream()
        .filter(field -> field.hasModifier(ApiModifier.STATIC))
        .collect(Collectors.toList());
    List<ApiField> annotationMethods = api
        .getFields()
        .stream()
        .filter(field -> !field.hasModifier(ApiModifier.STATIC))
        .collect(Collectors.toList());
    List<JavaField> fields = toJavaFields(java, constants);
    List<JavaMethod> methods = toJavaAnnotationMethods(java, annotationMethods);
    return new JavaClass(api, java, fields, Collections.emptyList(), methods);
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

  private List<JavaField> toJavaFields(Class<?> javaClass, List<ApiField> fields)
  {
    return fields
        .stream()
        .map(field -> toJava(javaClass, field))
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
  }
  
  private JavaField toJava(Class<?> javaClass, ApiField field)
  {
    return new JavaFieldResolver(javaClass, field).resolve();
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
  
  private List<JavaMethod> toJavaAnnotationMethods(Class<?> javaClass, List<ApiField> fields)
  {
    return fields
       .stream()
       .map(field -> toJavaAnnotationMethod(javaClass, field))
       .filter(Objects::nonNull)
       .collect(Collectors.toList());
  }
  
  private JavaMethod toJavaAnnotationMethod(Class<?> javaClass, ApiField field)
  {
    return new JavaAnnotationMethodResolver(javaClass, field).resolve();
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
