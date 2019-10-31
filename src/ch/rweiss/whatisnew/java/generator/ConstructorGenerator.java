package ch.rweiss.whatisnew.java.generator;

import java.lang.reflect.Constructor;
import java.lang.reflect.GenericArrayType;
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
import ch.rweiss.whatisnew.java.model.ApiConstructor;
import ch.rweiss.whatisnew.java.model.Version;

class ConstructorGenerator
{
  private final ClassGenerator classGenerator;
  private final ApiConstructor apiConstructor;
  private final Printer printer;

  ConstructorGenerator(ClassGenerator classGenerator, ApiConstructor apiConstructor)
  {
    this.classGenerator = classGenerator;
    this.apiConstructor = apiConstructor;
    this.printer = classGenerator.getPrinter();
  }

  public void generate()
  {
    Constructor<?> constructor = getJavaConstructor();
    if (constructor == null)
    {
      return;
    }
    generateJavaDoc();
    generateDeclaration(constructor);
    generateBody(constructor);
  }
  
  private void generateJavaDoc()
  {
    printer.print("/**");
    printer.println();
    printer.print(" * Example call to new constructor {@link ");
    printer.print(classGenerator.getJavaClass().getSimpleName());
    printer.print("#");
    printer.print(classGenerator.getJavaClass().getSimpleName());
    printer.print("}");
    printer.println();
    printer.print(" * @since ");
    if (apiConstructor.getSince().equals(Version.UNDEFINED))
    {
      printer.print(classGenerator.getApiClass().getSince());
    }
    else
    {
      printer.print(apiConstructor.getSince());
    }
    printer.println();
    printer.print(" * @see ");
    printer.print(classGenerator.getJavaClass().getSimpleName());
    printer.print("#");
    printer.print(classGenerator.getJavaClass().getSimpleName());
    printer.println();
    printer.print(" */");
    printer.println();
  }
  
  private void generateDeclaration(Constructor<?> constructor)
  {
    printer.print("public ");

    new TypeVariablesDeclarationGenerator(classGenerator.getImports(), printer, constructor.getTypeParameters()).generate();
    if (ArrayUtils.isNotEmpty(constructor.getTypeParameters()))
    {
      printer.print(' ');
    }
    printer.print(classGenerator.getName().getGeneratorSimpleName());
    printer.print('(');
    generateParameterList(constructor);
    printer.print(')');
    generateThrows(constructor.getExceptionTypes());
    printer.println();
  }
  
  private void generateParameterList(Constructor<?> constructor)
  {
    printer.forEachPrint(constructor.getParameters(), ", ", this::generateParameter);
  }
  
  Stream<Class<?>> getTypesToImport()
  {
    Constructor<?> constructor = getJavaConstructor();
    if (constructor == null)
    {
      return Stream.empty();
    }
    List<Class<?>> types = new ArrayList<>();
    types.addAll(Arrays.asList(constructor.getParameterTypes()));
    return types.stream();
  }
  
  private Constructor<?> getJavaConstructor()
  {
    Constructor<?> constructor = resolve();
    if (constructor == null)
    {
      System.err.println("Could not generate constructor for class "+classGenerator.getJavaClass().getName());
      return null;
    }
    if (Modifier.isPublic(constructor.getModifiers()))
    {
      return constructor;
    }
    return null;
  }

  private Constructor<?> resolve()
  {
    Constructor<?> constructor =  findDeclaredWithParamTypes();
    if (constructor != null)
    {
      return constructor;
    }
    constructor = findDeclaredWithTypeVariableParams();
    if (constructor != null)
    {
      return constructor;
    }
    constructor = findPublicWithParamTypes();
    if (constructor != null)
    {
      return constructor;
    }
    return null;
  }

  private Constructor<?> findDeclaredWithParamTypes()
  {
    try
    {
      Class<?>[] parameterTypes = apiConstructor
              .getArgumentTypes()
              .stream()
              .map(ClassGenerator::getJavaClass)
              .toArray(Class[]::new);
      return classGenerator.getJavaClass().getDeclaredConstructor(parameterTypes);
    }
    catch(WhatIsNewInException | NoSuchMethodException | SecurityException ex)
    {
      return null;
    }
  }

  private Constructor<?> findDeclaredWithTypeVariableParams()
  {
    return Arrays
            .stream(classGenerator.getJavaClass().getDeclaredConstructors())
            .filter(method -> matches(method))
            .findAny()
            .orElse(null);
  }

  private Constructor<?>  findPublicWithParamTypes()
  {
    try
    {
      Class<?>[] parameterTypes = apiConstructor
              .getArgumentTypes()
              .stream()
              .map(ClassGenerator::getJavaClass)
              .toArray(Class[]::new);
      return classGenerator.getJavaClass().getConstructor(parameterTypes);
    }
    catch(WhatIsNewInException | NoSuchMethodException | SecurityException ex)
    {
      return null;
    }
  }

  private boolean matches(Constructor<?> constructor)
  {
    return constructor.getParameterCount() == apiConstructor.getArgumentTypes().size() &&
           matches(constructor.getParameters(), apiConstructor.getArgumentTypes());
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
  
  private void generateParameter(Parameter parameter)
  {
    new TypeNameGenerator(classGenerator.getImports(), printer, parameter.getParameterizedType()).generate();
    printer.print(' ');
    printer.print(parameter.getName());
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
  
  private void generateBody(Constructor<?> constructor)
  {
    printer.print("{");
    printer.println();    
    printer.indent(4);
    generateCall(constructor);
    printer.indent(2);
    printer.print("}");
    printer.println(); 
    printer.println(); 
  }
  
  private void generateCall(Constructor<?> constructor)
  {
    new TypeNameGenerator(classGenerator.getImports(), printer, classGenerator.getJavaClass()).generate();
    printer.print(" testee = new ");
    printer.print(classGenerator.getJavaClass().getSimpleName());
    printer.print("(");
    generateParameterNameList(constructor);
    printer.print(");");
    printer.println();
  }

  private void generateParameterNameList(Constructor<?> constructor)
  {
    Arrays
        .stream(constructor.getParameters())
        .map(Parameter::getName)
        .collect(printer.toPrintedList(", "));
  }







}
