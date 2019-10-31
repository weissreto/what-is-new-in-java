package ch.rweiss.whatisnew.java.model;

import java.util.List;

public class ApiConstructor
{
  private final List<String> argumentTypes;
  private final Version since;

  public ApiConstructor(List<String> argumentTypes, Version since)
  {
    this.argumentTypes = argumentTypes;
    this.since = since;
  }

  public Version getSince()
  {
    return since;
  }

  public List<String> getArgumentTypes()
  {
    return argumentTypes;
  }
}
