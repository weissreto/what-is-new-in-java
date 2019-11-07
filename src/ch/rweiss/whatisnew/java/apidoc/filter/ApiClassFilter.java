package ch.rweiss.whatisnew.java.apidoc.filter;

import java.util.List;
import java.util.stream.Collectors;

import ch.rweiss.whatisnew.java.apidoc.model.ApiClass;
import ch.rweiss.whatisnew.java.apidoc.model.ApiConstructor;
import ch.rweiss.whatisnew.java.apidoc.model.ApiField;
import ch.rweiss.whatisnew.java.apidoc.model.ApiMethod;
import ch.rweiss.whatisnew.java.apidoc.model.Version;

class ApiClassFilter
{
  private final ApiClass apiClass;
  private final List<Version> versions;

  ApiClassFilter(ApiClass apiClass, List<Version> versions)
  {
    this.apiClass = apiClass;
    this.versions = versions;
  }

  public ApiClass filter()
  {
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
    List<ApiField> filteredFields = apiClass
            .getFields()
            .stream()
            .filter(this::filter)
            .collect(Collectors.toList());

    if (filteredMethods.isEmpty() && filteredConstructors.isEmpty() && filteredFields.isEmpty())
    {
      return null;
    }
    return new ApiClass(apiClass.getName(), filteredConstructors, filteredMethods, filteredFields, apiClass.getSince());
  }
  
  private boolean filter(ApiMethod method)
  {
    return filter(method.getSince());
  }

  private boolean filter(ApiConstructor constructor)
  {
    return filter(constructor.getSince());
  }

  private boolean filter(ApiField field)
  {
    return filter(field.getSince());
  }

  private boolean filter(Version version)
  {
    if (version == Version.UNDEFINED)
    {
      version = apiClass.getSince();
    }
    if (versions.isEmpty())
    {
      return true;
    }
    return versions.contains(version);
  }

}
