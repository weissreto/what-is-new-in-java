package ch.rweiss.whatisnew.java.generator;

import ch.rweiss.whatisnew.java.generator.model.JavaConstructor;
import ch.rweiss.whatisnew.java.generator.model.JavaParameter;

class JavaDocConstructorReferenceGenerator extends AbstractJavaDocReferenceGenerator
{
  private final ClassGenerator classGenerator;
  private final JavaConstructor constructor;

  JavaDocConstructorReferenceGenerator(ClassGenerator classGenerator, JavaConstructor constructor)
  {
    super(classGenerator.getPrinter());
    this.classGenerator = classGenerator;
    this.constructor = constructor;
  }
  
  @Override
  protected void generateRef()
  {
    printer.print(classGenerator.getClazz().getSimpleName());
    printer.print("#");
    printer.print(classGenerator.getClazz().getSimpleName());
    printer.print('(');
    printer.forEachPrint(constructor.getParameters(), ", ", this::generateParameter);
    printer.print(')');
  }
  
  private void generateParameter(JavaParameter parameter)
  {
    Class<?> type = parameter.getType();
    if (parameter.isVarArgs())
    {
      type = type.getComponentType();
    }
    new RawTypeNameGenerator(classGenerator.getImports(), printer, type).generate();
    if (parameter.isVarArgs())
    {
      printer.print("...");
    }
  }
}
