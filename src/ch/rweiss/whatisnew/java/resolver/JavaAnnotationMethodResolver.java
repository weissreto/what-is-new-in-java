package ch.rweiss.whatisnew.java.resolver;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import ch.rweiss.whatisnew.java.apidoc.model.ApiField;
import ch.rweiss.whatisnew.java.generator.model.JavaMethod;

class JavaAnnotationMethodResolver
{
  private final Class<?> javaClass;
  private final ApiField api;

  JavaAnnotationMethodResolver(Class<?> javaClass, ApiField api)
  {
    this.javaClass = javaClass;
    this.api = api;
  }

  JavaMethod resolve()
  {
    Method java = toJava();
    if (java == null)
    {
      System.err.println("Method "+api.getName()+"() not found for class "+javaClass.getName());
      return null;
    }
    if (!Modifier.isPublic(java.getModifiers()))
    {
      return null;
    }
    return new JavaMethod(api, java);
  }

  private Method toJava()
  {
    try
    {
      return javaClass.getDeclaredMethod(api.getName());
    }
    catch (NoSuchMethodException | SecurityException ex)
    {
      return null;
    }
  }
}
