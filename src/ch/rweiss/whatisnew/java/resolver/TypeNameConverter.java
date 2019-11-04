package ch.rweiss.whatisnew.java.resolver;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;

import ch.rweiss.whatisnew.java.type.AbstractTypeVisitor;
import ch.rweiss.whatisnew.java.type.Joiner;
import ch.rweiss.whatisnew.java.type.TypeUtil;

class TypeNameConverter extends AbstractTypeVisitor
{
  private final Type type;
  private final StringBuilder builder = new StringBuilder();

  TypeNameConverter(Type type)
  {
    this.type = type;
  }

  String convertToString()
  {
    visit(type);
    return builder.toString();
  }
  
  @Override
  protected void visit(Class<?> clazz)
  {
    if (clazz.isArray())
    {
      visit(clazz.getComponentType());
      builder.append("[]");
      return;
    }
    builder.append(TypeUtil.toRawName(clazz));
  }
  
  @Override
  protected void visit(GenericArrayType genericArrayType)
  {
    super.visit(genericArrayType);
    builder.append("[]");
  }
  
  @Override
  protected void visit(ParameterizedType parameterizedType)
  {
    visit(parameterizedType.getRawType());
    if (parameterizedType.getActualTypeArguments().length > 0)
    {
      builder.append("<");
      Joiner.forEach(parameterizedType.getActualTypeArguments(), () -> builder.append(","), this::visit);
      builder.append(">");
    }
  }
  
  @Override
  protected void visit(TypeVariable<?> typeVariable)
  {    
    builder.append(typeVariable.getName());
  }
  
  @Override
  protected void visit(WildcardType wildcardType)
  {
    builder.append('?');
    // ? super Object must be declared 
    // ? extends Object must not be declared explicit
    Type[] lowerBounds = wildcardType.getLowerBounds(); 
    if (lowerBounds.length > 0)
    {
      builder.append(" super ");
      Joiner.forEach(wildcardType.getLowerBounds(), () -> builder.append(" & "), this::visit);
    }
    Type[] upperBounds = TypeUtil.getBoundsWithoutObject(wildcardType.getUpperBounds());
    if (upperBounds.length > 0)
    {
      builder.append(" extends ");
      Joiner.forEach(wildcardType.getUpperBounds(), () -> builder.append(" & "), this::visit);
    }
  }
}
