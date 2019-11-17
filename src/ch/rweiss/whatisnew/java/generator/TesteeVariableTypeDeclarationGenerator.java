package ch.rweiss.whatisnew.java.generator;

import java.lang.reflect.TypeVariable;

class TesteeVariableTypeDeclarationGenerator
{
  private final Printer printer;
  private final Class<?> type;

  TesteeVariableTypeDeclarationGenerator(Printer printer, Class<?> type)
  {
    this.printer = printer;
    this.type = type;
  }

  void generate()
  {
    printer.print(type.getSimpleName());
    TypeVariable<?>[] typeParameters = type.getTypeParameters();
    if (typeParameters.length > 0)
    {
      printer.print('<');
      printer.forEachPrint(typeParameters, ", ", typeParam -> printer.print(typeParam.getName()));
      printer.print('>');
    }
  }
}