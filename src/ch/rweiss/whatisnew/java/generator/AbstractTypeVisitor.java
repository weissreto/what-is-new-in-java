package ch.rweiss.whatisnew.java.generator;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.HashSet;
import java.util.Set;

public class AbstractTypeVisitor
{
  private Set<Type> visited = new HashSet<>();
  public void visit(Type type)
  {
    if (visited.contains(type))
    {
      return;
    }
    visited.add(type);
    if (type instanceof Class)
    {
      visit((Class<?>)type);
    }
    else if (type instanceof GenericArrayType)
    {
      visit((GenericArrayType)type);
    }
    else if (type instanceof ParameterizedType)
    {
      visit((ParameterizedType)type);
    }
    else if (type instanceof TypeVariable)
    {
      visit((TypeVariable<?>)type);
    }
    else if (type instanceof WildcardType)
    {
      visit((WildcardType)type);
    }
    else
    {
      throw new IllegalArgumentException(
          "Parameter type must either be a generic array, parameterized type, type variable "+
          "or wildcard type but was " + type.getClass());
    }
    visited.remove(type);
  }
  
  protected void visit(@SuppressWarnings("unused") Class<?> clazz)
  {
  }

  protected void visit(GenericArrayType genericArrayType)
  {
    visit(genericArrayType.getGenericComponentType());
  }
  
  protected void visit(ParameterizedType parameterizedType)
  {
    visit(parameterizedType.getRawType());
    for (Type argument : parameterizedType.getActualTypeArguments())
    {
      visit(argument);
    }
  }
  
  protected void visit(TypeVariable<?> typeVariable)
  {
    for (Type bound : typeVariable.getBounds())
    {
      visit(bound);
    }
  }
  
  protected void visit(WildcardType wildcardType)
  {
    for (Type lowerBound : wildcardType.getLowerBounds())
    {
      visit(lowerBound);
    }
    for (Type upperBound : wildcardType.getUpperBounds())
    {
      visit(upperBound);
    }      
  }
}
