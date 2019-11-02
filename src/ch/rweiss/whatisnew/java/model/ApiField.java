package ch.rweiss.whatisnew.java.model;

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
	return "ApiField [name="+name+", type="+type+" modifiers="+modifiers+", since="+since+"]";
  }
}
