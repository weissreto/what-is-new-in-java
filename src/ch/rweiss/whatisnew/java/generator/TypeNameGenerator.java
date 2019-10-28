package ch.rweiss.whatisnew.java.generator;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.apache.commons.lang3.StringUtils;

class TypeNameGenerator
{
  private final Imports imports;
  private final Printer printer;
  private final Type type;

  TypeNameGenerator(Imports imports, Printer printer, Type type)
  {
    this.imports = imports;
    this.printer = printer;
    this.type = type;
  }

  public void generate()
  {
    if (type instanceof Class)
    {
      generate((Class<?>)type);
      return;
    }
    if (type instanceof ParameterizedType)
    {
      ParameterizedType pType = (ParameterizedType)type;
      generateRawName(pType.getRawType());
      printer.print('<');
      printer.forEachPrint(
          pType.getActualTypeArguments(), 
          ", ", 
          typeArg -> new TypeNameGenerator(imports, printer, typeArg).generate());
      printer.print('>');
      return;
    }
    generateRawName(type);
  }
  
  private void generate(Class<?> clazz)
  {
    if (clazz.isArray())
    {
      generate(clazz.getComponentType());
      printer.print("[]");
    }
    generateRawName(clazz);
    new TypeVariablesGenerator(printer, clazz.getTypeParameters()).generate();
  }  

  private void generateRawName(Type rawType)
  {
    String typeName = rawType.getTypeName();
    String packageName = StringUtils.substringBeforeLast(typeName, ".");
    if ("java.lang".equals(packageName))
    {
      printer.print(StringUtils.substringAfterLast(typeName, "."));
      return;
    }
    typeName = imports.toSimpleNameIfImported(typeName);
    printer.print(typeName);
  }
  
}
