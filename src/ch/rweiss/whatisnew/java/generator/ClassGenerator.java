package ch.rweiss.whatisnew.java.generator;

import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
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
  private final BufferedWriter writer;
  private final ApiClass apiClass;
  private int indent;
  private boolean newLine = true;
  private boolean needsCreateMethod = false;
  private final Imports imports = new Imports();

  ClassGenerator(ClassName name, ApiClass apiClass, BufferedWriter writer)
  {
    this.name = name;
    this.apiClass = apiClass;
    this.writer = writer;
  }

  void generate()
  {
    generatePackage();
    generateImports();
    generateClass();
  }

  private void generatePackage()
  {
    print("package ");
    print(name.getGeneratorPackageName());
    print(";");
  }

  private void generateImports()
  {
    println();
    println();
    imports.add(name.getApiFullQualifiedName());
    apiClass
        .getMethods()
        .stream()
        .map(this::getJavaMethod)
        .flatMap(ClassGenerator::getTypeNamesToImport)
        .forEach(imports::add);
    imports.forEach(this::generateImport);
    println();
  }
  
  private void generateImport(String className)
  {
    print("import ");
    print(className);
    print(";");
    println();
  }

  private void generateClass()
  {
    print("public final class ");
    print(name.getGeneratorSimpleName());
    Class<?> clazz = getJavaClass();
    print(new TypeVariablesConverter(clazz.getTypeParameters()).toName());
    println();
    print("{");
    println();
    indent(2);
    apiClass.getMethods().forEach(this::generate);
    generateCreateMethod();
    indent(0);
    print("}");
    println();
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
    print("/**");
    println();
    print(" * New method <code>"+method.getName()+"</code>");
    println();
    print(" * @Since ");
    print(method.getSince());
    println();
    print(" */");
    println();
  }

  private void generateMethodDeclaration(ApiMethod apiMethod, Method method)
  {
    print("public ");
    if (Modifier.isStatic(method.getModifiers()))
    {
      print("static ");
    }

    print(new TypeVariablesConverter(method.getTypeParameters()).toName());

    print(new TypeConverter(imports, method.getGenericReturnType()).toName());
    print(" ");
    print(apiMethod.getName());
    print("(");
    print(getParameterList(method));
    print(")");
    generateThrows(method.getExceptionTypes());
    println();
  }

  private void generateThrows(Class<?>[] exceptionTypes)
  {
    if (ArrayUtils.isEmpty(exceptionTypes))
    {
      return;
    }
    print(" throws ");
    print(Arrays.stream(exceptionTypes).map(Class::getName).collect(Collectors.joining(", ")));    
  }

  private void generateMethodBody(ApiMethod apiMethod, Method method)
  {
    print("{");
    println();    
    indent(4);
    generateTesteeObjectCreation(method);
    generateMethodCall(apiMethod, method);
    generateReturn(method);
    indent(2);
    print("}");
    println(); 
    println(); 
  }

  private void generateTesteeObjectCreation(Method method)
  {
    if (!Modifier.isStatic(method.getModifiers()))
    {
      print(getJavaClass());
      print(" ");
      print("testee = create();");
      needsCreateMethod = true;
      println();
      println();
    }
  }

  private void generateMethodCall(ApiMethod apiMethod, Method method)
  {
    if (!method.getReturnType().equals(Void.TYPE))
    {
      print(new TypeConverter(imports, method.getGenericReturnType()).toName());
      print(" result = ");
    }
    if (Modifier.isStatic(method.getModifiers()))
    {
      print(imports.toSimpleNameIfImported(getJavaClass().getName()));
      print(".");
    }
    else
    {
      print("testee.");
    }
    print(apiMethod.getName());
    print("(");
    print(getParameterNameList(method));
    print(");");
    println();
  }

  private void generateReturn(Method method)
  {
    if (!method.getReturnType().equals(Void.TYPE))
    {
      print("return result;");
      println();
    }
  }

  private void generateCreateMethod()
  {
    if (!needsCreateMethod)
    {
      return;
    }
    print("private <O> O create()");    
    println();
    print("{");
    println();
    indent(4);
    print("return null;");
    println();
    indent(2);
    print("}");
    println();
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
  
  private static String getParameterNameList(Method method)
  {
    return Arrays
        .stream(method.getParameters())
        .map(Parameter::getName)
        .collect(Collectors.joining(", "));
  }
  
  private String getParameterList(Method method)
  {
    return Arrays
        .stream(method.getParameters())
        .map(parameter -> new TypeConverter(imports, parameter.getParameterizedType()).toName()+" "+parameter.getName())
        .collect(Collectors.joining(", "));
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
  
  private void print(Class<?> type)
  {
    print(new TypeConverter(imports, type).toName());
  }
  
  private void print(Object text) 
  {
    try
    {
      if (newLine )
      {
        for (int pos = 0; pos < indent; pos++)
        {
          writer.append(' ');
        }
      }      
      writer.append(text.toString());
      newLine = false;
    }
    catch(IOException ex)
    {
      throw new WhatIsNewInException(ex);
    }
  }
  
  private void println()
  {
    try
    {
      writer.append('\n');
      newLine = true;
    }
    catch(IOException ex)
    {
      throw new WhatIsNewInException(ex);
    }
  }

  private void indent(int ind)
  {
    this.indent = ind;
  }
}
