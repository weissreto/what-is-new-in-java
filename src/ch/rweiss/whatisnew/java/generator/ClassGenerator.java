package ch.rweiss.whatisnew.java.generator;

import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
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
    generateTypeParameters(clazz.getTypeParameters());
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

    generateTypeParameters(method.getTypeParameters());

    print(toTypeName(method.getGenericReturnType()));
    print(" ");
    print(apiMethod.getName());
    print("(");
    print(getParameterList(method));
    print(")");
    generateThrows(method.getExceptionTypes());
    println();
  }

  private void generateTypeParameters(TypeVariable<?>[] typeParameters)
  {
    if (ArrayUtils.isNotEmpty(typeParameters))
    {      
      print("<");
      print(Arrays
          .stream(typeParameters)
          .map(this::toTypeVariableName)
          .collect(Collectors.joining(", ")));
      print("> ");
    }
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
      print(toTypeName(method.getGenericReturnType()));
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
        .map(parameter -> toTypeName(parameter.getParameterizedType())+" "+parameter.getName())
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

  private String toTypeName(Type type)
  {
    if (type instanceof Class)
    {
      toTypeName((Class<?>)type);
    }
    if (type instanceof ParameterizedType)
    {
      ParameterizedType pType = (ParameterizedType)type;
      StringBuilder typeName = new StringBuilder(128);
      typeName.append(toRawTypeName(pType.getRawType()));
      typeName.append('<');
      typeName.append(
          Arrays
              .stream(pType.getActualTypeArguments())
              .map(this::toTypeName)
              .collect(Collectors.joining(", ")));
      typeName.append('>');
      return typeName.toString();
    }
    return toRawTypeName(type);
  }
  
  private String toTypeName(Class<?> type)
  {
    if (type.isArray())
    {
      return toTypeName(type.getComponentType())+"[]";
    }
    StringBuilder typeName = new StringBuilder(128);
    typeName.append(toRawTypeName(type));
    if (ArrayUtils.isNotEmpty(type.getTypeParameters()))
    {
      typeName.append('<');
      typeName.append(
          Arrays
              .stream(type.getTypeParameters())
              .map(this::toTypeVariableName)
              .collect(Collectors.joining(", ")));
      typeName.append('>');
    }
    return typeName.toString();
  }
  
  private String toTypeVariableName(TypeVariable<?> variable)
  {
    return variable.getName();
//    if (ArrayUtils.isEmpty(variable.getBounds()))
//    {
//      return "?";
//    }
//    String typeName = variable.getBounds()[0].getTypeName();
//    if (Object.class.getName().equals(typeName))
//    {
//      return "?";
//    }
//    return typeName;    
  }
  
  private String toRawTypeName(Type type)
  {
    String typeName = type.getTypeName();
    String packageName = StringUtils.substringBeforeLast(typeName, ".");
    if ("java.lang".equals(packageName))
    {
      return StringUtils.substringAfterLast(typeName, ".");
    }
    typeName = imports.toSimpleNameIfImported(typeName);
    return typeName;
  }

  private void print(Class<?> type)
  {
    print(toTypeName(type));
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
