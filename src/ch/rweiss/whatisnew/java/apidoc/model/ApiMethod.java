package ch.rweiss.whatisnew.java.apidoc.model;

import java.util.List;
import java.util.stream.Collectors;

public class ApiMethod
{
  private final String name;
  private final String returnType;
  private final List<ApiArgument> arguments;
  private final List<ApiModifier> modifiers;
  private final Version since;

  public ApiMethod(String name, String returnType, List<ApiArgument> arguments, List<ApiModifier> modifiers,
          Version since)
  {
    this.name = name;
    this.returnType = returnType;
    this.arguments = arguments;
    this.modifiers = modifiers;
    this.since = since;
  }

  public Version getSince()
  {
    return since;
  }

  public String getName()
  {
    return name;
  }

  public List<ApiArgument> getArguments()
  {
    return arguments;
  }

  public List<String> getRawArgumentTypes()
  {
    return arguments
            .stream()
            .map(ApiArgument::getRawType)
            .collect(Collectors.toList());
  }

  public List<String> getArgumentTypes()
  {
    return arguments
            .stream()
            .map(ApiArgument::getType)
            .collect(Collectors.toList());
  }

  @Override
  public String toString()
  {
    return "ApiMethod [name=" + name + ", returnType=" + returnType + ", arguments=" + arguments
            + ", modifiers=" + modifiers + ", since=" + since + "]";
  }
}
