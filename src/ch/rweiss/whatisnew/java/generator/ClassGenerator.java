package ch.rweiss.whatisnew.java.generator;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;

import ch.rweiss.whatisnew.java.WhatIsNewInException;
import ch.rweiss.whatisnew.java.model.ApiClass;
import ch.rweiss.whatisnew.java.model.ApiMethod;

class ClassGenerator
{
  private final ClassName name;
  private final Printer printer;
  private final ApiClass apiClass;
  private boolean needsCreateMethod = false;
  private final Imports imports = new Imports();

  ClassGenerator(ClassName name, ApiClass apiClass, Printer printer)
  {
    this.name = name;
    this.apiClass = apiClass;
    this.printer = printer;
  }

  void generate()
  {
    generatePackage();
    generateImports();
    generateClass();
  }

  private void generatePackage()
  {
    printer.print("package ");
    printer.print(name.getGeneratorPackageName());
    printer.print(";");
  }

  private void generateImports()
  {
    printer.println();
    printer.println();
    imports.add(name.getApiFullQualifiedName());
    apiClass
        .getMethods()
        .stream()
        .map(this::getJavaMethod)
        .flatMap(ClassGenerator::getTypeNamesToImport)
        .forEach(imports::add);
    imports.forEach(this::generateImport);
    printer.println();
  }
  
  private void generateImport(String className)
  {
    printer.print("import ");
    printer.print(className);
    printer.print(";");
    printer.println();
  }

  private void generateClass()
  {
    printer.print("public final class ");
    printer.print(name.getGeneratorSimpleName());
    Class<?> clazz = getJavaClass();
    new TypeVariablesGenerator(printer, clazz.getTypeParameters()).generate();
    printer.println();
    printer.print("{");
    printer.println();
    printer.indent(2);
    apiClass.getMethods().forEach(this::generate);
    generateCreateMethod();
    printer.indent(0);
    printer.print("}");
    printer.println();
  }

  void generate(ApiMethod apiMethod)
  {
    Method method = getJavaMethod(apiMethod);

    generateJavaDoc(apiMethod);
    generateMethodDeclaration(apiMethod, method);
    generateMethodBody(apiMethod, method);
  }

  private Method getJavaMethod(ApiMethod apiMethod)
  {
    try
    {
      Class<?> clazz = getJavaClass();
      Class<?>[] parameterTypes = apiMethod
              .getArgumentTypes()
              .stream()
              .map(ClassGenerator::getJavaClass)
              .toArray(Class[]::new);
      return clazz.getDeclaredMethod(apiMethod.getName(), parameterTypes);
    }
    catch (NoSuchMethodException | SecurityException ex)
    {
      throw new WhatIsNewInException(ex);
    }
  }

  private void generateJavaDoc(ApiMethod method)
  {
    printer.print("/**");
    printer.println();
    printer.print(" * New method <code>"+method.getName()+"</code>");
    printer.println();
    printer.print(" * @Since ");
    printer.print(method.getSince());
    printer.println();
    printer.print(" */");
    printer.println();
  }

  private void generateMethodDeclaration(ApiMethod apiMethod, Method method)
  {
    printer.print("public ");
    if (Modifier.isStatic(method.getModifiers()))
    {
      printer.print("static ");
    }

    new TypeVariablesGenerator(printer, method.getTypeParameters()).generate();
    new TypeNameGenerator(imports, printer, method.getGenericReturnType()).generate();
    printer.print(" ");
    printer.print(apiMethod.getName());
    printer.print("(");
    generateParameterList(method);
    printer.print(")");
    generateThrows(method.getExceptionTypes());
    printer.println();
  }

  private void generateThrows(Class<?>[] exceptionTypes)
  {
    if (ArrayUtils.isEmpty(exceptionTypes))
    {
      return;
    }
    printer.print(" throws ");
    Arrays
        .stream(exceptionTypes)
        .map(Class::getName)
        .collect(printer.toPrintedList(", "));    
  }

  private void generateMethodBody(ApiMethod apiMethod, Method method)
  {
    printer.print("{");
    printer.println();    
    printer.indent(4);
    generateTesteeObjectCreation(method);
    generateMethodCall(apiMethod, method);
    generateReturn(method);
    printer.indent(2);
    printer.print("}");
    printer.println(); 
    printer.println(); 
  }

  private void generateTesteeObjectCreation(Method method)
  {
    if (!Modifier.isStatic(method.getModifiers()))
    {
      new TypeNameGenerator(imports, printer, getJavaClass()).generate();
      printer.print(" ");
      printer.print("testee = create();");
      needsCreateMethod = true;
      printer.println();
      printer.println();
    }
  }

  private void generateMethodCall(ApiMethod apiMethod, Method method)
  {
    if (!method.getReturnType().equals(Void.TYPE))
    {
      new TypeNameGenerator(imports, printer, method.getGenericReturnType()).generate();
      printer.print(" result = ");
    }
    if (Modifier.isStatic(method.getModifiers()))
    {
      printer.print(imports.toSimpleNameIfImported(getJavaClass().getName()));
      printer.print(".");
    }
    else
    {
      printer.print("testee.");
    }
    printer.print(apiMethod.getName());
    printer.print("(");
    generateParameterNameList(method);
    printer.print(");");
    printer.println();
  }

  private void generateReturn(Method method)
  {
    if (!method.getReturnType().equals(Void.TYPE))
    {
      printer.print("return result;");
      printer.println();
    }
  }

  private void generateCreateMethod()
  {
    if (!needsCreateMethod)
    {
      return;
    }
    printer.print("private <O> O create()");    
    printer.println();
    printer.print("{");
    printer.println();
    printer.indent(4);
    printer.print("return null;");
    printer.println();
    printer.indent(2);
    printer.print("}");
    printer.println();
  }

  private Class<?> getJavaClass()
  {
    return getJavaClass(name.getApiFullQualifiedName());
  }
  
  private static Class<?> getJavaClass(String fullQualifiedName)
  {
    if (fullQualifiedName.endsWith("..."))
    {
      fullQualifiedName = StringUtils.removeEnd(fullQualifiedName, "...") + "[]";
    }
    try
    {
      return ClassUtils.getClass(fullQualifiedName);
    }
    catch (ClassNotFoundException ex)
    {
      throw new WhatIsNewInException(ex);
    }
  }
  
  private static Stream<String> getTypeNamesToImport(Method method)
  {
    List<String> typeNames = new ArrayList<>();
    String returnType = toImportTypeName(method.getReturnType());
    if (returnType != null)
    {
      typeNames.add(returnType);
    }
    Arrays
        .stream(method.getParameterTypes())
        .map(ClassGenerator::toImportTypeName)
        .filter(Objects::nonNull)
        .forEach(typeNames::add);    
    return typeNames.stream();
  }
  
  private void generateParameterNameList(Method method)
  {
    Arrays
        .stream(method.getParameters())
        .map(Parameter::getName)
        .collect(printer.toPrintedList(", "));
  }
  
  private void generateParameterList(Method method)
  {
    printer.forEachPrint(method.getParameters(), ", ", this::generateParameter);
  }
    
  private void generateParameter(Parameter parameter)
  {
    new TypeNameGenerator(imports, printer, parameter.getParameterizedType()).generate();
    printer.print(' ');
    printer.print(parameter.getName());
  }
  
  private static String toImportTypeName(Class<?> type)
  {
    if (type.isArray())
    {
      return toImportTypeName(type.getComponentType());
    }
    if (type.isPrimitive())
    {
      return null;
    }
    if (StringUtils.startsWith(type.getTypeName(), "java.lang."))
    {
      return null;
    }
    return type.getTypeName();
  }   
}
