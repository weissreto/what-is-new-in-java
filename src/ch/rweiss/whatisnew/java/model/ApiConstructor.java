package ch.rweiss.whatisnew.java.model;

import java.util.List;
import java.util.stream.Collectors;

public class ApiConstructor
{
  private final List<ApiArgument> arguments;
  private final List<ApiModifier> modifiers;
  private final Version since;

  public ApiConstructor(List<ApiArgument> arguments, List<ApiModifier> modifiers, Version since)
  {
    this.arguments = arguments;
    this.modifiers = modifiers;
    this.since = since;
  }

  public Version getSince()
  {
    return since;
  }

  public List<ApiArgument> getArguments()
  {
    return arguments;
  }

  public List<String> getArgumentTypes()
  {
    return arguments
        .stream()
        .map(ApiArgument::getRawType)
        .collect(Collectors.toList());
  }

  @Override
  public String toString()
  {
    return "ApiConstructor [arguments=" + arguments + ", modifiers=" + modifiers + ", since=" + since + "]";
  }
}
