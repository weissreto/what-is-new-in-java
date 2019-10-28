package ch.rweiss.whatisnew.java.generator;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

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

  void generate()
  {
    if (type instanceof Class)
    {
      generate((Class<?>)type);
      return;
    }
    if (type instanceof ParameterizedType)
    {
      ParameterizedType pType = (ParameterizedType)type;
      new RawTypeNameGenerator(imports, printer, pType.getRawType()).generate();
      printer.print('<');
      printer.forEachPrint(
          pType.getActualTypeArguments(), 
          ", ", 
          typeArg -> new TypeNameGenerator(imports, printer, typeArg).generate());
      printer.print('>');
      return;
    }
    new RawTypeNameGenerator(imports, printer, type).generate();
  }
  
  private void generate(Class<?> clazz)
  {
    new RawTypeNameGenerator(imports, printer, clazz).generate();
    new TypeVariablesGenerator(printer, clazz.getTypeParameters()).generate();
  }  
}
