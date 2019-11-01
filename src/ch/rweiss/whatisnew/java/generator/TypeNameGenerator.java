package ch.rweiss.whatisnew.java.generator;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;

class TypeNameGenerator extends AbstractTypeVisitor
{
  private final Imports imports;
  private final Printer printer;
  private final Type type;

  TypeNameGenerator(Imports imports, Printer printer, Type type)
  {
    this.imports = imports;
    this.printer = printer;
    this.type = type;
  }

  void generate()
  {
    visit(type);
  }
  
  @Override
  protected void visit(Class<?> clazz)
  {
    if (clazz.isArray())
    {
      visit(clazz.getComponentType());
      printer.print("[]");
      return;
    }
    new RawTypeNameGenerator(imports, printer, clazz).generate();
  }
  
  @Override
  protected void visit(GenericArrayType genericArrayType)
  {
    super.visit(genericArrayType);
    printer.print("[]");
  }
  
  @Override
  protected void visit(ParameterizedType parameterizedType)
  {
    visit(parameterizedType.getRawType());
//    new RawTypeNameGenerator(imports, printer, parameterizedType.getRawType()).generate();
    if (parameterizedType.getActualTypeArguments().length > 0)
    {
      printer.print("<");
      printer.forEachPrint(parameterizedType.getActualTypeArguments(), ", ", this::visit);
      printer.print(">");
    }
  }
  
  @Override
  protected void visit(TypeVariable<?> typeVariable)
  {    
    printer.print(typeVariable.getName());
  }
  
  @Override
  protected void visit(WildcardType wildcardType)
  {
    printer.print('?');
    // ? super Object must be declared 
    // ? extends Object must not be declared explicit
    Type[] lowerBounds = wildcardType.getLowerBounds(); 
    if (lowerBounds.length > 0)
    {
      printer.print(" super ");
      printer.forEachPrint(wildcardType.getLowerBounds(), " & ", this::visit);
    }
    Type[] upperBounds = TypeUtil.getBoundsWithoutObject(wildcardType.getUpperBounds());
    if (upperBounds.length > 0)
    {
      printer.print(" extends ");
      printer.forEachPrint(wildcardType.getUpperBounds(), " & ", this::visit);
    }
  }
}
