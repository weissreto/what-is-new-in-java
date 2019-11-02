package ch.rweiss.whatisnew.java.generator.model;

import java.lang.reflect.Parameter;
import java.lang.reflect.Type;

import ch.rweiss.whatisnew.java.model.ApiArgument;

public class JavaParameter 
{
  private ApiArgument api;
  private Parameter java;

  public JavaParameter(ApiArgument api, Parameter java) 
  {
	this.api = api;
	this.java = java;
  }
  public Type getParameterizedType()
  {
	return java.getParameterizedType();
  }

  public String getName()
  {
	return api.getName(); 
  }
}
