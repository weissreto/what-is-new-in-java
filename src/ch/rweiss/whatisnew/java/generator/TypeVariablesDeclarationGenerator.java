package ch.rweiss.whatisnew.java.generator;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

import org.apache.commons.lang3.ArrayUtils;

class TypeVariablesDeclarationGenerator
{
  private final Printer printer;
  private final TypeVariable<?>[] variables;
  private final Imports imports;

  TypeVariablesDeclarationGenerator(Imports imports, Printer printer, TypeVariable<?>[] variables)
  {
    this.imports = imports;
    this.printer = printer;
    this.variables = variables;
  }

  void generate()
  {
    if (ArrayUtils.isEmpty(variables))
    {
      return;
    }
    printer.print('<');
    printer.forEachPrint(variables, ", ", this::generate);
    printer.print('>');
  }
  
  private void generate(TypeVariable<?> typeVariable)
  {
    printer.print(typeVariable.getName());
    Type[] bounds = TypeUtil.getBoundsWithoutObject(typeVariable.getBounds());
    if (bounds.length > 0)
    {
      printer.print(" extends ");
      printer.forEachPrint(bounds, " & ", bound -> new TypeNameGenerator(imports, printer, bound).generate());
    }
  }
}
