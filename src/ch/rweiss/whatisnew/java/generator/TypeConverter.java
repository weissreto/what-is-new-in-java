package ch.rweiss.whatisnew.java.generator;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

class TypeConverter
{
  private final Imports imports;
  private final Type type;

  TypeConverter(Imports imports, Type type)
  {
    this.imports = imports;
    this.type = type;
  }

  public String toName()
  {
    if (type instanceof Class)
    {
      toName((Class<?>)type);
    }
    if (type instanceof ParameterizedType)
    {
      ParameterizedType pType = (ParameterizedType)type;
      StringBuilder typeName = new StringBuilder(128);
      typeName.append(toRawName(pType.getRawType()));
      typeName.append('<');
      typeName.append(
          Arrays
              .stream(pType.getActualTypeArguments())
              .map(typeArg -> new TypeConverter(imports, typeArg).toName())
              .collect(Collectors.joining(", ")));
      typeName.append('>');
      return typeName.toString();
    }
    return toRawName(type);
  }
  
  private String toName(Class<?> clazz)
  {
    if (clazz.isArray())
    {
      return toName(clazz.getComponentType())+"[]";
    }
    StringBuilder typeName = new StringBuilder(128);
    typeName.append(toRawName(clazz));
    typeName.append(new TypeVariablesConverter(clazz.getTypeParameters()).toName());
    return typeName.toString();
  }  

  private String toRawName(Type rawType)
  {
    String typeName = rawType.getTypeName();
    String packageName = StringUtils.substringBeforeLast(typeName, ".");
    if ("java.lang".equals(packageName))
    {
      return StringUtils.substringAfterLast(typeName, ".");
    }
    typeName = imports.toSimpleNameIfImported(typeName);
    return typeName;
  }
  
}
