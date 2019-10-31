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
import java.util.stream.Stream;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import ch.rweiss.whatisnew.java.WhatIsNewInException;
import ch.rweiss.whatisnew.java.model.ApiMethod;
import ch.rweiss.whatisnew.java.model.Version;

class MethodGenerator
{
  private ApiMethod apiMethod;
  private Printer printer;
  private ClassGenerator classGenerator;

  MethodGenerator(ClassGenerator classGenerator, ApiMethod apiMethod)
  {
    this.classGenerator = classGenerator;
    this.apiMethod = apiMethod;
    this.printer = classGenerator.getPrinter();
  }

  void generate()
  {
    Method method = getJavaMethod();
    if (method == null)
    {
      return;
    }
    if (classGenerator.methodAlreadyGenerated(method))
    {
      return;
    }
    classGenerator.addGeneratedMethod(method);

    generateJavaDoc();
    generateDeclaration(method);
    generateBody(method);
  }
  
  private Method getJavaMethod()
  {
    Method method = resolve();
    if (method == null)
    {
      System.err.println("Could not generate method "+apiMethod.getName()+" for class "+classGenerator.getJavaClass().getName());
      return null;
    }
    if (Modifier.isPublic(method.getModifiers()))
    {
      return method;
    }
    return null;
  }

  private Method resolve()
  {
    Method method = findDeclaredWithParamTypes();
    if (method != null)
    {
      return method;
    }
    method = findDeclaredWithTypeVariableParams();
    if (method != null)
    {
      return method;
    }
    method = findPublicWithParamTypes();
    if (method != null)
    {
      return method;
    }
    return null;
  }

  private Method findDeclaredWithParamTypes()
  {
    try
    {
      Class<?>[] parameterTypes = apiMethod
              .getArgumentTypes()
              .stream()
              .map(ClassGenerator::getJavaClass)
              .toArray(Class[]::new);
      return classGenerator.getJavaClass().getDeclaredMethod(apiMethod.getName(), parameterTypes);
    }
    catch(WhatIsNewInException | NoSuchMethodException | SecurityException ex)
    {
      return null;
    }
  }

  private Method findDeclaredWithTypeVariableParams()
  {
    return Arrays
            .stream(classGenerator.getJavaClass().getDeclaredMethods())
            .filter(method -> matches(method))
            .findAny()
            .orElse(null);
  }

  private Method findPublicWithParamTypes()
  {
    try
    {
      Class<?>[] parameterTypes = apiMethod
              .getArgumentTypes()
              .stream()
              .map(ClassGenerator::getJavaClass)
              .toArray(Class[]::new);
      return classGenerator.getJavaClass().getMethod(apiMethod.getName(), parameterTypes);
    }
    catch(WhatIsNewInException | NoSuchMethodException | SecurityException ex)
    {
      return null;
    }
  }

  private boolean matches(Method method)
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

  private void generateJavaDoc()
  {
    printer.print("/**");
    printer.println();
    printer.print(" * Example call to new method {@link ");
    printer.print(classGenerator.getJavaClass().getSimpleName());
    printer.print("#");
    printer.print(apiMethod.getName());
    printer.print("}");
    printer.println();
    printer.print(" * @since ");
    if (apiMethod.getSince().equals(Version.UNDEFINED))
    {
      printer.print(classGenerator.getApiClass().getSince());
    }
    else
    {
      printer.print(apiMethod.getSince());
    }
    printer.println();
    printer.print(" * @see ");
    printer.print(classGenerator.getJavaClass().getSimpleName());
    printer.print("#");
    printer.print(apiMethod.getName());
    printer.println();
    printer.print(" */");
    printer.println();
  }

  private void generateDeclaration(Method method)
  {
    printer.print("public ");
    if (Modifier.isStatic(method.getModifiers()))
    {
      printer.print("static ");
    }

    new TypeVariablesDeclarationGenerator(classGenerator.getImports(), printer, method.getTypeParameters()).generate();
    if (ArrayUtils.isNotEmpty(method.getTypeParameters()))
    {
      printer.print(' ');
    }
    new TypeNameGenerator(classGenerator.getImports(), printer, method.getGenericReturnType()).generate();
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

  private void generateBody(Method method)
  {
    printer.print("{");
    printer.println();    
    printer.indent(4);
    generateTesteeObjectCreation(method);
    generateCall(method);
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
      new TypeNameGenerator(classGenerator.getImports(), printer, classGenerator.getJavaClass()).generate();
      printer.print(" ");
      printer.print("testee = create();");
      classGenerator.needsCreateMethod();
      printer.println();
      printer.println();
    }
  }

  private void generateCall(Method method)
  {
    if (!method.getReturnType().equals(Void.TYPE))
    {
      new TypeNameGenerator(classGenerator.getImports(), printer, method.getGenericReturnType()).generate();
      printer.print(" result = ");
    }
    if (Modifier.isStatic(method.getModifiers()))
    {
      new RawTypeNameGenerator(classGenerator.getImports(), printer, classGenerator.getJavaClass()).generate();
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
  
  Stream<Class<?>> getTypesToImport()
  {
    Method method = getJavaMethod();
    if (method == null)
    {
      return Stream.empty();
    }
    List<Class<?>> types = new ArrayList<>();
    types.add(method.getReturnType());
    types.addAll(Arrays.asList(method.getParameterTypes()));
    return types.stream();
  }
  
  private void generateParameter(Parameter parameter)
  {
    new TypeNameGenerator(classGenerator.getImports(), printer, parameter.getParameterizedType()).generate();
    printer.print(' ');
    printer.print(parameter.getName());
  }
}
