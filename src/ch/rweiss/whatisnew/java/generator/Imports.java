package ch.rweiss.whatisnew.java.generator;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.apache.commons.lang3.StringUtils;

class Imports
{
  private final List<String> typeNames = new ArrayList<>();
  private final List<String> simpleTypeNames = new ArrayList<>();
  
  void add(Class<?> type)
  {
    if (type.isArray())
    {
      add(type.getComponentType());
      return;
    }
    if (type.isPrimitive())
    {
      return;
    }
    String rawTypeName = RawTypeNameGenerator.toRawName(type.getTypeName());
    if (RawTypeNameGenerator.isJavaLangPackage(rawTypeName))
    {
      return;
    }
    String simpleName = toSimpleName(rawTypeName);
    if (simpleTypeNames.contains(simpleName))
    {
      return;
    }
    simpleTypeNames.add(simpleName);
    typeNames.add(rawTypeName);
  }

  void forEach(Consumer<String> consumer)
  {
    typeNames.forEach(consumer);
  }
  
  String toSimpleNameIfImported(String typeName)
  {
    if (typeNames.contains(typeName))
    {
      return toSimpleName(typeName);
    }
    return typeName;
  }

  private String toSimpleName(String typeName)
  {
    return StringUtils.substringAfterLast(typeName, ".");
  }
}
