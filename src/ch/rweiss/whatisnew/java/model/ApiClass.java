package ch.rweiss.whatisnew.java.model;

import java.util.List;

public class ApiClass
{
  private final String name;
  private final List<ApiField> fields;
  private final List<ApiConstructor> constructors;
  private final List<ApiMethod> methods;
  private final Version since;

  public ApiClass(String name, List<ApiConstructor> constructors, List<ApiMethod> methods,
          List<ApiField> fields, Version since)
  {
    this.name = name;
    this.constructors = constructors;
    this.methods = methods;
    this.fields = fields;
    this.since = since;
  }

  public String getName()
  {
    return name;
  }

  public List<ApiConstructor> getConstructors()
  {
    return constructors;
  }
  
  public List<ApiMethod> getMethods()
  {
    return methods;
  }
  
  public List<ApiField> getFields()
  {
    return fields;
  }
  
  public Version getSince()
  {
    return since;
  }
  
  @Override
  public String toString()
  {
    return "ApiClass [name="+name+", since="+since+"]";
  }
}
