package ch.rweiss.whatisnew.java.generator;

import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.apache.commons.lang3.ArrayUtils;

import ch.rweiss.whatisnew.java.generator.model.JavaConstructor;
import ch.rweiss.whatisnew.java.model.Version;

class ConstructorGenerator
{
  private final ClassGenerator classGenerator;
  private final JavaConstructor constructor;
  private final Printer printer;

  ConstructorGenerator(ClassGenerator classGenerator, JavaConstructor constructor)
  {
    this.classGenerator = classGenerator;
    this.constructor = constructor;
    this.printer = classGenerator.getPrinter();
  }

  public void generate()
  {
    generateJavaDoc();
    generateDeclaration();
    generateBody();
  }
  
  private void generateJavaDoc()
  {
    printer.print("/**");
    printer.println();
    printer.print(" * Example call to new constructor {@link ");
    printer.print(classGenerator.getClazz().getSimpleName());
    printer.print("#");
    printer.print(classGenerator.getClazz().getSimpleName());
    printer.print("}");
    printer.println();
    printer.print(" * @since ");
    if (constructor.getSince().equals(Version.UNDEFINED))
    {
      printer.print(classGenerator.getClazz().getSince());
    }
    else
    {
      printer.print(constructor.getSince());
    }
    printer.println();
    printer.print(" * @see ");
    printer.print(classGenerator.getClazz().getSimpleName());
    printer.print("#");
    printer.print(classGenerator.getClazz().getSimpleName());
    printer.println();
    printer.print(" */");
    printer.println();
  }
  
  private void generateDeclaration()
  {
    printer.print("public ");

    new TypeVariablesDeclarationGenerator(classGenerator.getImports(), printer, constructor.getTypeParameters()).generate();
    if (ArrayUtils.isNotEmpty(constructor.getTypeParameters()))
    {
      printer.print(' ');
    }
    printer.print(classGenerator.getClazz().getGeneratorSimpleName());
    printer.print('(');
    generateParameterList();
    printer.print(')');
    generateThrows();
    printer.println();
  }
  
  private void generateParameterList()
  {
    printer.forEachPrint(constructor.getParameters(), ", ", this::generateParameter);
  }
  
  Stream<Class<?>> getTypesToImport()
  {
    List<Class<?>> types = new ArrayList<>();
    types.addAll(Arrays.asList(constructor.getParameterTypes()));
    return types.stream();
  }
  
  private void generateParameter(Parameter parameter)
  {
    new TypeNameGenerator(classGenerator.getImports(), printer, parameter.getParameterizedType()).generate();
    printer.print(' ');
    printer.print(parameter.getName());
  }
  
  private void generateThrows()
  {
    Class<?>[] exceptionTypes = constructor.getExceptionTypes();
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
  
  private void generateBody()
  {
    printer.print("{");
    printer.println();    
    printer.indent(4);
    generateCall();
    printer.indent(2);
    printer.print("}");
    printer.println(); 
    printer.println(); 
  }
  
  private void generateCall()
  {
    new TypeNameGenerator(classGenerator.getImports(), printer, classGenerator.getClazz().getJava()).generate();
    printer.print(" testee = new ");
    printer.print(classGenerator.getClazz().getSimpleName());
    printer.print("(");
    generateParameterNameList();
    printer.print(");");
    printer.println();
  }

  private void generateParameterNameList()
  {
    Arrays
        .stream(constructor.getParameters())
        .map(Parameter::getName)
        .collect(printer.toPrintedList(", "));
  }
}