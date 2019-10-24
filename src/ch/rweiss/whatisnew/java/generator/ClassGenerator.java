package ch.rweiss.whatisnew.java.generator;

import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.stream.Collectors;

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

  ClassGenerator(ClassName name, ApiClass apiClass, BufferedWriter writer)
  {
    this.name = name;
    this.apiClass = apiClass;
    this.writer = writer;
  }

  void generate()
  {
    print("package ");
    print(name.getGeneratorPackageName());
    print(";");
    println();
    println();
    print("import ");
    print(name.getApiFullQualifiedName());
    print(";");
    println();
    println();
    print("public final class ");
    print(name.getGeneratorSimpleName());
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
  
  private void generateCreateMethod()
  {
    print("private <T> T create()");    
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

  void generate(ApiMethod apiMethod)
  {
    Method method = getJavaMethod(apiMethod);

    generateJavaDoc(apiMethod);
    println();
    print("public void ");
    print(apiMethod.getName());
    print("(");
    print(")");
    generateThrows(method.getExceptionTypes());
    println();
    print("{");
    println();    
    indent(4);
    if (!Modifier.isStatic(method.getModifiers()))
    {
      print(name.getApiSimpleName());
      print(" ");
      print("testee = create();");
      println();
    }
    for (Parameter parameter : method.getParameters())
    {
      print(parameter.getType());
      print(" ");
      print(parameter.getName());
      print(" = create();");
      println();
    }
    
    if (!method.getReturnType().equals(Void.TYPE))
    {
      print(method.getReturnType());
      print(" result = ");
    }
    if (Modifier.isStatic(method.getModifiers()))
    {
      print(name.getApiSimpleName());
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
    indent(2);
    print("}");
    println(); 
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

  private Method getJavaMethod(ApiMethod apiMethod)
  {
    try
    {
      Class<?> clazz = getJavaClass();
      Class<?>[] parameterTypes = apiMethod.getArgumentTypes().stream().map(this::getJavaClass).toArray(Class[]::new);
      return clazz.getDeclaredMethod(apiMethod.getName(), parameterTypes);
    }
    catch (NoSuchMethodException | SecurityException ex)
    {
      throw new WhatIsNewInException(ex);
    }
  }

  private Class<?> getJavaClass()
  {
    return getJavaClass(name.getApiFullQualifiedName());
  }
  
  private Class<?> getJavaClass(String fullQualifiedName)
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

  private String getParameterNameList(Method method)
  {
    return Arrays.stream(method.getParameters()).map(Parameter::getName).collect(Collectors.joining(", "));
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
  }

  private void print(Class<?> type)
  {
    if (type.isArray())
    {
      print(type.getComponentType());
      print("[]");
    }
    else
    {
      print(type.getName());
    }
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
