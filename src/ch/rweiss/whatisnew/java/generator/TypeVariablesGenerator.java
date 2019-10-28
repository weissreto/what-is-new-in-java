package ch.rweiss.whatisnew.java.generator;

import java.lang.reflect.TypeVariable;
import java.util.Arrays;

import org.apache.commons.lang3.ArrayUtils;

class TypeVariablesGenerator
{
  private final Printer printer;
  private final TypeVariable<?>[] variables;

  TypeVariablesGenerator(Printer printer, TypeVariable<?>[] variables)
  {
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
    Arrays
        .stream(variables)
        .map(TypeVariable::getName)
        .collect(printer.toPrintedList(", "));
    printer.print('>');
  }
}
