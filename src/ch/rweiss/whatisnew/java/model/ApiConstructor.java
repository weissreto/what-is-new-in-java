package ch.rweiss.whatisnew.java.model;

import java.util.List;

public class ApiConstructor
{
  private final boolean isVarArg;
  private final List<String> argumentTypes;
  private final Version since;

  public ApiConstructor(List<String> argumentTypes, boolean isVarArg, Version since)
  {
    this.argumentTypes = argumentTypes;
    this.isVarArg = isVarArg;
    this.since = since;
  }
}
