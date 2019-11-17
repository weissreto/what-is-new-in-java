package ch.rweiss.whatisnew.java.generator;

import ch.rweiss.whatisnew.java.generator.model.JavaField;

class JavaDocFieldReferenceGenerator extends AbstractJavaDocReferenceGenerator
{
  private final JavaField field;
  private final ClassGenerator classGenerator;

  JavaDocFieldReferenceGenerator(ClassGenerator classGenerator, JavaField field)
  {
    super(classGenerator.getPrinter());
    this.classGenerator = classGenerator;
    this.field = field;
  }

  @Override
  protected void generateRef()
  {
    printer.print(classGenerator.getClazz().getSimpleName());
    printer.print("#");
    printer.print(field.getName());
  }
}
