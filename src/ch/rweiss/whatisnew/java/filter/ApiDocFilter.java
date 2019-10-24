package ch.rweiss.whatisnew.java.filter;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import ch.rweiss.whatisnew.java.model.ApiClass;
import ch.rweiss.whatisnew.java.model.ApiDoc;
import ch.rweiss.whatisnew.java.model.ApiMethod;
import ch.rweiss.whatisnew.java.model.Version;

public class ApiDocFilter
{
  private final ApiDoc apiDoc;
  private final Version version;

  private ApiDocFilter(ApiDoc apiDoc, Version version)
  {
    this.apiDoc = apiDoc;
    this.version = version;
  }

  public static ApiDoc filter(ApiDoc apiDoc, Version version)
  {
    return new ApiDocFilter(apiDoc, version).filter();
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
    List<ApiMethod> filteredMethods = apiClass
            .getMethods()
            .stream()
            .filter(this::filter)
            .collect(Collectors.toList());
    if (filteredMethods.isEmpty())
    {
      return null;
    }
    return new ApiClass(apiClass.getName(), apiClass.getConstructors(), filteredMethods, apiClass.getFields());
  }
  
  private boolean filter(ApiMethod method)
  {
    return method.getSince().equals(version);
  }
}
