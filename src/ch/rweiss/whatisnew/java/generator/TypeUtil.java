package ch.rweiss.whatisnew.java.generator;

import java.lang.reflect.Type;
import java.util.Arrays;

public class TypeUtil
{
  public static boolean isJavaLangPackageAndTopLevelClass(Class<?> clazz)
  {
    return isJavaLangPackge(clazz) && isTopLevelClass(clazz);
  }

  private static boolean isJavaLangPackge(Class<?> clazz)
  {
    String packageName = clazz.getPackageName();
    return "java.lang".equals(packageName) ;
  }

  private static boolean isTopLevelClass(Class<?> clazz)
  {
    return clazz.getNestHost() == clazz;
  }

  public static String toRawName(Class<?> clazz)
  {
    if (isJavaLangPackageAndTopLevelClass(clazz) && clazz.getNestHost() == null)
    {
      return clazz.getSimpleName();
    }
    return clazz.getCanonicalName();
  }

  static Type[] getBoundsWithoutObject(Type[] bounds)
  {
    return Arrays
        .stream(bounds)
        .filter(type -> !Object.class.equals(type))
        .toArray(Type[]::new);  
  }

}
