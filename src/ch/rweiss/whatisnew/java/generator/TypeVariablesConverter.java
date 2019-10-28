package ch.rweiss.whatisnew.java.generator;

import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ArrayUtils;

class TypeVariablesConverter
{
  private final TypeVariable<?>[] variables;

  TypeVariablesConverter(TypeVariable<?>[] variables)
  {
    this.variables = variables;
  }

  String toName()
  {
    if (ArrayUtils.isEmpty(variables))
    {
      return "";
    }
    StringBuilder varNames = new StringBuilder();
    varNames.append('<');
    varNames.append(
          Arrays
              .stream(variables)
              .map(TypeVariablesConverter::toName)
              .collect(Collectors.joining(", ")));
    varNames.append('>');
    return varNames.toString();
  }
  
  private static String toName(TypeVariable<?> variable)
  {
    return variable.getName();
  }
}
