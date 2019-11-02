package ch.rweiss.whatisnew.java.model;

import org.apache.commons.lang3.StringUtils;

public class ApiArgument 
{
  private final String type;
  private final String name;
  
  public ApiArgument(String name, String type)
  {
	this.name = name;
	this.type = type;
  }
  
  public String getType()
  {
	return type;	  
  }
  
  public String getRawType()
  {
	  return StringUtils.substringBefore(getType(), "<");
  }
  
  public String getName()
  {
	return name;
  }
  
  @Override
  public String toString() 
  {
	return "ApiArgument [name="+name+", type="+type+"]";
  }
}
