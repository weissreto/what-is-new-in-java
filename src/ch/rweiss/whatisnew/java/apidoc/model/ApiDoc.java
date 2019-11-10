package ch.rweiss.whatisnew.java.apidoc.model;

import java.util.SortedSet;

public class ApiDoc
{
  private final SortedSet<ApiClass> classes;

  public ApiDoc(SortedSet<ApiClass> classes)
  {
    this.classes = classes;
  }

  public SortedSet<ApiClass> getClasses()
  {
    return classes;
  }

}
