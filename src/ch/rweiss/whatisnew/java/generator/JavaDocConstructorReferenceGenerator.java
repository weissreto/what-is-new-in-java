package ch.rweiss.whatisnew.java.generator;

import ch.rweiss.whatisnew.java.generator.model.JavaConstructor;

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
    printer.forEachPrint(constructor.getParameterTypes(), ", ", this::generateRawType);
    printer.print(')');
  }
  
  private void generateRawType(Class<?> type)
  {
    new RawTypeNameGenerator(classGenerator.getImports(), printer, type).generate();
  }
}
