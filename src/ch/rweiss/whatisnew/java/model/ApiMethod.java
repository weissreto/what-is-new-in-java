package ch.rweiss.whatisnew.java.model;

import java.util.List;

public class ApiMethod
{
  private final String name;
  private final List<String> argumentTypes;
  private final Version since;

  public ApiMethod(String name, List<String> argumentTypes, Version since)
  {
    this.name = name;
    this.argumentTypes = argumentTypes;
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

  public List<String> getArgumentTypes()
  {
    return argumentTypes;
  }
}
