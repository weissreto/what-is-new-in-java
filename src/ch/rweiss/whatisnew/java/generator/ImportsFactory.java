package ch.rweiss.whatisnew.java.generator;

import java.lang.reflect.Parameter;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;

import ch.rweiss.whatisnew.java.generator.model.JavaClass;
import ch.rweiss.whatisnew.java.generator.model.JavaConstructor;
import ch.rweiss.whatisnew.java.generator.model.JavaMethod;
import ch.rweiss.whatisnew.java.generator.model.JavaParameter;

class ImportsFactory
{
  private final JavaClass clazz;
  private final Imports imports = new Imports();
  
  public ImportsFactory(JavaClass clazz)
  {
    this.clazz = clazz;
  }

  static Imports createFor(JavaClass clazz)
  {
    return new ImportsFactory(clazz).create();
  }

  private Imports create()
  {
    new ImportTypeVisitor(imports).visit(clazz.getJava());
    clazz.getConstructors().forEach(this::createFor);
    clazz.getMethods().forEach(this::createFor);
    return imports;
  }
  
  private void createFor(JavaConstructor constructor)
  {
    Arrays.stream(constructor.getTypeParameters()).forEach(this::createFor);
    Arrays.stream(constructor.getParameters()).forEach(this::createFor);
    Arrays.stream(constructor.getExceptionTypes()).forEach(this::createFor);
  }
  
  private void createFor(JavaMethod method)
  {
    Arrays.stream(method.getTypeParameters()).forEach(this::createFor);
    new ImportTypeVisitor(imports).visit(method.getGenericReturnType());
    Arrays.stream(method.getParameters()).forEach(this::createFor);
    Arrays.stream(method.getExceptionTypes()).forEach(this::createFor);
  }
  
  private void createFor(TypeVariable<?> typeVariable)
  {
    new ImportTypeVisitor(imports).visit(typeVariable);
  }
  
  private void createFor(JavaParameter parameter)
  {
    new ImportTypeVisitor(imports).visit(parameter.getParameterizedType());
  }

  private void createFor(Class<?> exceptionClazz)
  {
    imports.add(exceptionClazz);
  }

}
