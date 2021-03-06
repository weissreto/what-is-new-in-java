package ch.rweiss.whatisnew.java.apidoc.model;

import java.util.List;

public class ApiField
{
  private final String type;
  private final String name;
  private final Version since;
  private List<ApiModifier> modifiers;

  public ApiField(String name, String type, List<ApiModifier> modifiers, Version since)
  {
    this.name = name;
    this.type = type;
    this.modifiers = modifiers;
    this.since = since;
  }

  @Override
  public String toString()
  {
    return "ApiField [name=" + name + ", type=" + type + " modifiers=" + modifiers + ", since=" + since + "]";
  }

  public String getName()
  {
    return name;
  }

  public Version getSince()
  {
    return since;
  }

  public boolean hasModifier(ApiModifier modifier)
  {
    return modifiers.contains(modifier);
  }
}
