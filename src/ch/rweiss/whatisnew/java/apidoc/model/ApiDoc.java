package ch.rweiss.whatisnew.java.apidoc.model;

import java.util.List;

public class ApiDoc
{
  private final List<ApiClass> classes;

  public ApiDoc(List<ApiClass> classes)
  {
    this.classes = classes;
  }

  public List<ApiClass> getClasses()
  {
    return classes;
  }

}
