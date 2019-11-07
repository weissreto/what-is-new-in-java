package ch.rweiss.whatisnew.java.generator;

import ch.rweiss.whatisnew.java.apidoc.model.Version;
import ch.rweiss.whatisnew.java.generator.model.JavaField;

public class FieldGenerator
{
  private final ClassGenerator classGenerator;
  private final JavaField field;
  private final Printer printer;

  public FieldGenerator(ClassGenerator classGenerator, JavaField field)
  {
    this.classGenerator = classGenerator;
    this.printer = classGenerator.getPrinter();
    this.field = field;
  }

  public void generate()
  {
    generateJavaDoc();
    generateDeclaration();
  }
  
  private void generateJavaDoc()
  {
    printer.print("/**");
    printer.println();
    printer.print(" * Example usage of the new field {@link ");
    printer.print(classGenerator.getClazz().getSimpleName());
    printer.print("#");
    printer.print(field.getName());
    printer.print("}");
    printer.println();
    printer.print(" * @since ");
    if (field.getSince().equals(Version.UNDEFINED))
    {
      printer.print(classGenerator.getClazz().getSince());
    }
    else
    {
      printer.print(field.getSince());
    }
    printer.println();
    printer.print(" * @see ");
    printer.print(classGenerator.getClazz().getSimpleName());
    printer.print("#");
    printer.print(field.getName());
    printer.println();
    printer.print(" */");
    printer.println();
  }
  
  private void generateDeclaration()
  {
    printer.print("public ");
    if (field.isStatic())
    {
      printer.print("static ");
    }
    printer.print(" final ");

    new TypeNameGenerator(classGenerator.getImports(), printer, field.getGenericReturnType()).generate();

    printer.print(' ');
    printer.print(field.getName());
    printer.print(" = ");
    if (field.isStatic())
    {
      printer.print(classGenerator.getClazz().getSimpleName());
    }
    else
    {
      printer.print("$$$()");
      classGenerator.needsCreateMethod();
    }
    printer.print(".");
    printer.print(field.getName());
    printer.print(";");
    printer.println();
    printer.println();
  }
}
