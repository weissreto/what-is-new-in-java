package ch.rweiss.whatisnew.java.generator;

import ch.rweiss.whatisnew.java.generator.model.JavaMethod;
import ch.rweiss.whatisnew.java.generator.model.JavaParameter;

class JavaDocMethodReferenceGenerator extends AbstractJavaDocReferenceGenerator
{
  private final ClassGenerator classGenerator;
  private final JavaMethod method;

  JavaDocMethodReferenceGenerator(ClassGenerator classGenerator, JavaMethod method)
  {
    super(classGenerator.getPrinter());
    this.classGenerator = classGenerator;
    this.method = method;
  }
 
  @Override
  protected void generateRef()
  {
    printer.print(classGenerator.getClazz().getSimpleName());
    printer.print("#");
    printer.print(method.getName());
    printer.print('(');
    printer.forEachPrint(method.getParameters(), ", ", this::generateParameter);
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
