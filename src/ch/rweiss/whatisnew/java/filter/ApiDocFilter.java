package ch.rweiss.whatisnew.java.filter;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import ch.rweiss.whatisnew.java.model.ApiClass;
import ch.rweiss.whatisnew.java.model.ApiConstructor;
import ch.rweiss.whatisnew.java.model.ApiDoc;
import ch.rweiss.whatisnew.java.model.ApiMethod;
import ch.rweiss.whatisnew.java.model.Version;

public class ApiDocFilter
{
  private final ApiDoc apiDoc;
  private final List<Version> versions;

  private ApiDocFilter(ApiDoc apiDoc, List<Version> versions)
  {
    this.apiDoc = apiDoc;
    this.versions = versions;
  }

  public static ApiDoc filter(ApiDoc apiDoc, List<Version> versions)
  {
    return new ApiDocFilter(apiDoc, versions).filter();
  }

  private ApiDoc filter()
  {
    List<ApiClass> filteredClasses = apiDoc
        .getClasses()
        .stream()
        .map(this::filter)
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
    return new ApiDoc(filteredClasses);
  }
  
  private ApiClass filter(ApiClass apiClass)
  {
    if (filter(apiClass.getSince()))
    {
      return apiClass;
    }
    List<ApiMethod> filteredMethods = apiClass
            .getMethods()
            .stream()
            .filter(this::filter)
            .collect(Collectors.toList());
    List<ApiConstructor> filteredConstructors = apiClass
            .getConstructors()
            .stream()
            .filter(this::filter)
            .collect(Collectors.toList());
    if (filteredMethods.isEmpty() && filteredConstructors.isEmpty())
    {
      return null;
    }
    return new ApiClass(apiClass.getName(), filteredConstructors, filteredMethods, apiClass.getFields(), apiClass.getSince());
  }
  
  private boolean filter(ApiMethod method)
  {
    return filter(method.getSince());
  }

  private boolean filter(ApiConstructor constructor)
  {
    return filter(constructor.getSince());
  }

  private boolean filter(Version version)
  {
    if (versions.isEmpty())
    {
      return true;
    }
    return versions.contains(version);
  }
}
