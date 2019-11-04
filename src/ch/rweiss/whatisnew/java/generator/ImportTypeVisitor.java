package ch.rweiss.whatisnew.java.generator;

import ch.rweiss.whatisnew.java.type.AbstractTypeVisitor;

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
