package ch.rweiss.whatisnew.java.generator;

import ch.rweiss.whatisnew.java.generator.model.JavaMethod;

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
    printer.forEachPrint(method.getParameterTypes(), ", ", this::generateRawType);
    printer.print(')');
  }
  
  private void generateRawType(Class<?> type)
  {
    new RawTypeNameGenerator(classGenerator.getImports(), printer, type).generate();
  }
}
