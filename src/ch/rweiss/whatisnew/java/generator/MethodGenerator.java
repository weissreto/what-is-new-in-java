package ch.rweiss.whatisnew.java.generator;

import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.apache.commons.lang3.ArrayUtils;

import ch.rweiss.whatisnew.java.generator.model.JavaMethod;
import ch.rweiss.whatisnew.java.model.Version;

class MethodGenerator
{
  private JavaMethod method;
  private Printer printer;
  private ClassGenerator classGenerator;

  MethodGenerator(ClassGenerator classGenerator, JavaMethod method)
  {
    this.classGenerator = classGenerator;
    this.method = method;
    this.printer = classGenerator.getPrinter();
  }

  void generate()
  {
    if (classGenerator.methodAlreadyGenerated(method.getJava()))
    {
      return;
    }
    classGenerator.addGeneratedMethod(method.getJava());

    generateJavaDoc();
    generateDeclaration();
    generateBody();
  }

  private void generateJavaDoc()
  {
    printer.print("/**");
    printer.println();
    printer.print(" * Example call to new method {@link ");
    printer.print(classGenerator.getClazz().getSimpleName());
    printer.print("#");
    printer.print(method.getName());
    printer.print("}");
    printer.println();
    printer.print(" * @since ");
    if (method.getSince().equals(Version.UNDEFINED))
    {
      printer.print(classGenerator.getClazz().getSince());
    }
    else
    {
      printer.print(method.getSince());
    }
    printer.println();
    printer.print(" * @see ");
    printer.print(classGenerator.getClazz().getSimpleName());
    printer.print("#");
    printer.print(method.getName());
    printer.println();
    printer.print(" */");
    printer.println();
  }

  private void generateDeclaration()
  {
    printer.print("public ");
    if (method.isStatic())
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
    printer.print(method.getName());
    printer.print('(');
    generateParameterList();
    printer.print(')');
    generateThrows();
    printer.println();
  }

  private void generateThrows()
  {
    Class<?>[] exceptionTypes = method.getExceptionTypes();
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
    generateTesteeObjectCreation();
    generateCall();
    generateReturn();
    printer.indent(2);
    printer.print("}");
    printer.println(); 
    printer.println(); 
  }

  private void generateTesteeObjectCreation()
  {
    if (!method.isStatic())
    {
      new TypeNameGenerator(classGenerator.getImports(), printer, classGenerator.getClazz().getJava()).generate();
      printer.print(" ");
      printer.print("testee = create();");
      classGenerator.needsCreateMethod();
      printer.println();
      printer.println();
    }
  }

  private void generateCall()
  {
    if (!method.getReturnType().equals(Void.TYPE))
    {
      new TypeNameGenerator(classGenerator.getImports(), printer, method.getGenericReturnType()).generate();
      printer.print(" result = ");
    }
    if (method.isStatic())
    {
      new RawTypeNameGenerator(classGenerator.getImports(), printer, classGenerator.getClazz().getJava()).generate();
      printer.print(".");
    }
    else
    {
      printer.print("testee.");
    }
    printer.print(method.getName());
    printer.print("(");
    generateParameterNameList();
    printer.print(");");
    printer.println();
  }

  private void generateReturn()
  {
    if (!method.getReturnType().equals(Void.TYPE))
    {
      printer.print("return result;");
      printer.println();
    }
  }
  
  private void generateParameterNameList()
  {
    Arrays
        .stream(method.getParameters())
        .map(Parameter::getName)
        .collect(printer.toPrintedList(", "));
  }
  
  private void generateParameterList()
  {
    printer.forEachPrint(method.getParameters(), ", ", this::generateParameter);
  }
  
  Stream<Class<?>> getTypesToImport()
  {
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