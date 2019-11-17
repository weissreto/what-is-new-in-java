package ch.rweiss.whatisnew.java.generator;

abstract class AbstractJavaDocReferenceGenerator
{
  protected final Printer printer;
  
  protected AbstractJavaDocReferenceGenerator(Printer printer)
  {
    this.printer = printer;
  }
  
  void generateLink()
  {
    printer.print("{@link ");
    generateRef();
    printer.print("}");
  }

  void generateSee()
  {
    printer.print(" * @see ");
    generateRef();
  }

  abstract protected void generateRef();
}
