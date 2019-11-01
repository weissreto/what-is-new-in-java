package ch.rweiss.whatisnew.java.generator;

public class ImportTypeVisitor extends AbstractTypeVisitor
{
  private Imports imports;

  public ImportTypeVisitor(Imports imports)
  {
    this.imports = imports;
  }
  
  @Override
  public void visit(Class<?> clazz)
  {
    imports.add(clazz);
  }
  
}
