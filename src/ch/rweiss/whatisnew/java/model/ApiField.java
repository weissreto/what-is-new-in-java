package ch.rweiss.whatisnew.java.model;

public class ApiField
{
  private final boolean isStatic;
  private final String type;
  private final String name;
  private final Version since;

  public ApiField(String name, String type, boolean isStatic, Version since)
  {
    this.name = name;
    this.type = type;
    this.isStatic = isStatic;
    this.since = since;
  }
}
