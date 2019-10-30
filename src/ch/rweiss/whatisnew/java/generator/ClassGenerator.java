package ch.rweiss.whatisnew.java.generator;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
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
import ch.rweiss.whatisnew.java.model.Version;

class ClassGenerator
{
  private final ClassName name;
  private final Printer printer;
  private final ApiClass apiClass;
  private boolean needsCreateMethod = false;
  private final Imports imports = new Imports();
  private final List<Version> versions;

  ClassGenerator(ClassName name, ApiClass apiClass, List<Version> versions, Printer printer)
  {
    this.name = name;
    this.apiClass = apiClass;
    this.versions = versions;
    this.printer = printer;
  }

  boolean generate()
  {
    Class<?> clazz = getJavaClass();
    if (!Modifier.isPublic(clazz.getModifiers()))
    {
      return false;
    }
    
    generatePackage();
    generateImports();
    generateClass();
    return true;
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
    imports.add(getJavaClass());
    apiClass
        .getMethods()
        .stream()
        .map(this::getJavaMethod)
        .filter(Objects::nonNull)
        .flatMap(ClassGenerator::getTypeToImport)
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
    generateJavaDoc();
    printer.print("public final class ");
    printer.print(name.getGeneratorSimpleName());
    Class<?> clazz = getJavaClass();
    new TypeVariablesDeclarationGenerator(imports, printer, clazz.getTypeParameters()).generate();
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

  private void generateJavaDoc()
  {
    printer.print("/**");
    printer.println();
    printer.print(" * This source file was generated by WhatIsNewInJava<br>");
    printer.println();
    printer.print(" *");
    printer.println();
    printer.print(" * This class provides an example call to each method in class {@link ");
    printer.print(name.getApiSimpleName());
    printer.print("}");
    printer.println();
    printer.print(" * that were newly introduced in");
    if (versions.isEmpty()) 
    {
      printer.print(" any");
    }
    printer.print(" Java versions ");
    printer.forEachPrint(versions, ", ", version -> printer.print(version));
    printer.print(".<br>");
    printer.println();
    printer.print(" *");
    printer.println();
    printer.print(" * {@link ");
    printer.print(name.getApiSimpleName());
    if (versions.contains(apiClass.getSince()))
    {
      printer.print("} is a completely new class");
    }
    else
    {
      printer.print("} is an old class but has new fields, constructors or methods");
    }
    printer.println();
    printer.print(" * @since ");
    printer.print(apiClass.getSince());
    printer.println();
    printer.print(" * @see ");
    printer.print(name.getApiSimpleName());
    printer.println();
    printer.print(" */");
    printer.println();
  }

  void generate(ApiMethod apiMethod)
  {
    Method method = getJavaMethod(apiMethod);
    if (method == null)
    {
      return;
    }
    generateJavaDoc(apiMethod);
    generateMethodDeclaration(apiMethod, method);
    generateMethodBody(apiMethod, method);
  }

  private Method getJavaMethod(ApiMethod apiMethod)
  {
    Method method = resolveMethod(apiMethod);
    if (Modifier.isPublic(method.getModifiers()))
    {
      return method;
    }
    return null;
  }

  private Method resolveMethod(ApiMethod apiMethod)
  {
    Class<?> clazz = getJavaClass();
    try
    {
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
    catch(WhatIsNewInException ex)
    {
      return Arrays
        .stream(clazz.getDeclaredMethods())
        .filter(method -> matches(method, apiMethod))
        .findAny()
        .orElseThrow(() -> new WhatIsNewInException("Could not find method "+apiMethod.getName()));
    }
  }

  private boolean matches(Method method, ApiMethod apiMethod)
  {
    return method.getName().equals(apiMethod.getName()) &&
           method.getParameterCount() == apiMethod.getArgumentTypes().size() &&
           matches(method.getParameters(), apiMethod.getArgumentTypes());
  }

  private boolean matches(Parameter[] parameters, List<String> argumentTypes)
  {
    for (int pos = 0; pos < parameters.length; pos++)
    {
      Parameter parameter = parameters[pos];
      String argumentType = argumentTypes.get(pos);
      if (argumentType.endsWith("..."))
      {
        argumentType = StringUtils.removeEnd(argumentType, "...") + "[]";
      }
      String parameterType = RawTypeNameGenerator.toRawName(parameter.getType());
      Type type = parameter.getParameterizedType();
      if (type instanceof TypeVariable)
      {
        parameterType = ((TypeVariable<?>) type).getName();
      }
      else if (type instanceof GenericArrayType)
      {
        parameterType = ((GenericArrayType) type).getGenericComponentType().getTypeName()+"[]";
      }
      if (!parameterType.equals(argumentType))
      {
        return false;
      }
    }
    return true;
  }

  private void generateJavaDoc(ApiMethod method)
  {
    printer.print("/**");
    printer.println();
    printer.print(" * Example call to new method {@link ");
    printer.print(name.getApiSimpleName());
    printer.print("#");
    printer.print(method.getName());
    printer.print("}");
    printer.println();
    printer.print(" * @since ");
    if (method.getSince().equals(Version.UNDEFINED))
    {
      printer.print(apiClass.getSince());
    }
    else
    {
      printer.print(method.getSince());
    }
    printer.println();
    printer.print(" * @see ");
    printer.print(name.getApiSimpleName());
    printer.print("#");
    printer.print(method.getName());
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

    new TypeVariablesDeclarationGenerator(imports, printer, method.getTypeParameters()).generate();
    if (ArrayUtils.isNotEmpty(method.getTypeParameters()))
    {
      printer.print(' ');
    }
    new TypeNameGenerator(imports, printer, method.getGenericReturnType()).generate();
    printer.print(' ');
    printer.print(apiMethod.getName());
    printer.print('(');
    generateParameterList(method);
    printer.print(')');
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
      new RawTypeNameGenerator(imports, printer, getJavaClass()).generate();
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
    return getJavaClass(name);
  }
  
  static Class<?> getJavaClass(ClassName name)
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
  
  private static Stream<Class<?>> getTypeToImport(Method method)
  {
    List<Class<?>> types = new ArrayList<>();
    types.add(method.getReturnType());
    types.addAll(Arrays.asList(method.getParameterTypes()));
    return types.stream();
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
}
