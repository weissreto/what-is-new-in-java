package ch.rweiss.whatisnew.java.apidoc.filter;

import java.util.List;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import ch.rweiss.whatisnew.java.apidoc.model.ApiClass;
import ch.rweiss.whatisnew.java.apidoc.model.ApiDoc;
import ch.rweiss.whatisnew.java.apidoc.model.Version;

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
    SortedSet<ApiClass> filteredClasses = apiDoc
        .getClasses()
        .stream()
        .map(this::filter)
        .filter(Objects::nonNull)
        .collect(Collectors.toCollection(TreeSet::new));
    return new ApiDoc(filteredClasses);
  }
  
  private ApiClass filter(ApiClass apiClass)
  {
    return new ApiClassFilter(apiClass, versions).filter();
  }
}
