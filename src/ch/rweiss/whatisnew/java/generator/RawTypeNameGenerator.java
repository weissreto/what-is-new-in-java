package ch.rweiss.whatisnew.java.generator;

import java.lang.reflect.Type;

import org.apache.commons.lang3.StringUtils;

class RawTypeNameGenerator
{
  private final Imports imports;
  private final Printer printer;
  private final Type type;

  RawTypeNameGenerator(Imports imports, Printer printer, Type type)
  {
    this.imports = imports;
    this.printer = printer;
    this.type = type;
  }
  
  void generate()
  {
    String typeName = toRawName(type);
    if (isJavaLangPackage(typeName))
    {
      typeName = StringUtils.substringAfterLast(typeName, ".");
    }
    else
    {
      typeName = imports.toSimpleNameIfImported(typeName);
    }
    printer.print(typeName);
  }

  static String toRawName(Type rawType)
  {
    String typeName = rawType.getTypeName();
    return toRawName(typeName);
  }

  static String toRawName(String typeName)
  {
    typeName = typeName.replace('$', '.');
    return typeName;
  }

  public static boolean isJavaLangPackage(String typeName)
  {
    String packageName = StringUtils.substringBeforeLast(typeName, ".");
    return "java.lang".equals(packageName);
  }
}
