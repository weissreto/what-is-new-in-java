package ch.rweiss.whatisnew.java.generator;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;

import ch.rweiss.whatisnew.java.type.AbstractTypeVisitor;

public class ArrayTypeConverter extends AbstractTypeVisitor
{
  private Type arrayType;
  private Type componentType;

  public ArrayTypeConverter(Type arrayType)
  {
    this.arrayType = arrayType;
  }

  public Type toComponentType()
  {
    visit(arrayType);
    return componentType;
  }
  
  @Override
  protected void visit(Class<?> clazz)
  {
    componentType = clazz.getComponentType();
  }
  
  @Override
  protected void visit(GenericArrayType genericArrayType)
  {
    componentType = genericArrayType.getGenericComponentType();
  }
  
  @Override
  protected void visit(ParameterizedType parameterizedType)
  {
    throw new IllegalArgumentException("Expected array type but was "+parameterizedType);
  }
  
  @Override
  protected void visit(TypeVariable<?> typeVariable)
  {
    throw new IllegalArgumentException("Expected array type but was "+typeVariable);
  }
  
  @Override
  protected void visit(WildcardType wildcardType)
  {
    throw new IllegalArgumentException("Expected array type but was "+wildcardType);
  }
}
