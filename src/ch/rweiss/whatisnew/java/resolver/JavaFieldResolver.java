package ch.rweiss.whatisnew.java.resolver;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import ch.rweiss.whatisnew.java.generator.model.JavaField;
import ch.rweiss.whatisnew.java.model.ApiField;

public class JavaFieldResolver
{
  private final Class<?> javaClass;
  private final ApiField api;

  public JavaFieldResolver(Class<?> javaClass, ApiField api)
  {
    this.javaClass = javaClass;
    this.api = api;
  }

  public JavaField resolve()
  {
    Field java = toJava();
    if (java == null)
    {
      System.err.println("Field "+api.getName()+" not found for class "+javaClass.getName());
      return null;
    }
    if (!Modifier.isPublic(java.getModifiers()))
    {
      return null;
    }
    return new JavaField(api, java);
  }

  private Field toJava()
  {
    try
    {
      return javaClass.getField(api.getName());
    }
    catch (NoSuchFieldException | SecurityException ex)
    {
      try
      {
        return javaClass.getDeclaredField(api.getName());
      }
      catch (NoSuchFieldException | SecurityException ex1)
      {
        return null;
      }
    }
  }
}
