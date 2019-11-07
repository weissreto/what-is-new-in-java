package ch.rweiss.whatisnew.java.resolver;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import ch.rweiss.whatisnew.java.WhatIsNewInException;
import ch.rweiss.whatisnew.java.apidoc.model.ApiClass;
import ch.rweiss.whatisnew.java.apidoc.model.ApiDoc;
import ch.rweiss.whatisnew.java.generator.model.JavaApi;
import ch.rweiss.whatisnew.java.generator.model.JavaClass;

public class JavaResolver
{
  private ApiDoc apiDoc;

  public JavaResolver(ApiDoc apiDoc)
  {
    this.apiDoc = apiDoc;
  }

  public JavaApi toJavaApi()
  {
    List<JavaClass> classes = apiDoc
        .getClasses()
        .stream()
        .map(this::toJava)
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
    return new JavaApi(classes);
  }
  
  private JavaClass toJava(ApiClass api)
  {
    try
    {
      return new JavaClassResolver(api).resolve();
    }
    catch(WhatIsNewInException ex)
    {
      System.err.println("Could not find java class for "+api.getName());
      return null;
    }
  }
}
