package ch.rweiss.whatisnew.java.generator;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;

import ch.rweiss.whatisnew.java.type.AbstractTypeVisitor;
import ch.rweiss.whatisnew.java.type.TypeUtil;

public class RawTypeNameGenerator extends AbstractTypeVisitor
{
  private final Imports imports;
  private final Printer printer;
  private final Type type;

  RawTypeNameGenerator(Imports imports, Printer printer, Type type)
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
    if (TypeUtil.isJavaLangPackageAndTopLevelClass(clazz))
    {
      printer.print(clazz.getSimpleName());
      return;
    }
    String typeName = imports.toSimpleNameIfImported(clazz.getCanonicalName());
    printer.print(typeName);
  }
  
  @Override
  protected void visit(GenericArrayType genericArrayType)
  {
    throw new UnsupportedOperationException("Do not know raw type of "+ genericArrayType);
  }
  
  @Override
  protected void visit(ParameterizedType parameterizedType)
  {
    throw new UnsupportedOperationException("Do not know raw type of "+ parameterizedType);
  }
  
  @Override
  protected void visit(TypeVariable<?> typeVariable)
  {
    throw new UnsupportedOperationException("Do not know raw type of "+ typeVariable);
  }
  
  @Override
  protected void visit(WildcardType wildcardType)
  {
    throw new UnsupportedOperationException("Do not know raw type of "+ wildcardType);
  }
}
