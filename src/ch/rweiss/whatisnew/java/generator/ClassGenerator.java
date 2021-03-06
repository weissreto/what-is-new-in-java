package ch.rweiss.whatisnew.java.generator;

import java.lang.reflect.Modifier;
import java.util.List;
import java.util.stream.Stream;

import ch.rweiss.whatisnew.java.apidoc.model.Version;
import ch.rweiss.whatisnew.java.generator.model.JavaClass;
import ch.rweiss.whatisnew.java.generator.model.JavaConstructor;
import ch.rweiss.whatisnew.java.generator.model.JavaField;
import ch.rweiss.whatisnew.java.generator.model.JavaMethod;

class ClassGenerator
{
  private final Printer printer;
  private final JavaClass clazz;
  private boolean needsCreateMethod = false;
  private final Imports imports;
  private final List<Version> versions;

  ClassGenerator(JavaClass api, List<Version> versions, Printer printer)
  {
    this.clazz = api;
    this.versions = versions;
    this.printer = printer;
    this.imports = ImportsFactory.createFor(clazz);
  }

  void generate()
  {
    generatePackage();
    generateImports();
    generateClass();
  }

  private void generatePackage()
  {
    printer.print("package ");
    printer.print(clazz.getGeneratorPackageName());
    printer.print(";");
  }

  private void generateImports()
  {
    printer.println();
    printer.println();
    imports.forEach(this::generateImport);
    printer.println();
  }
  
  Stream<Class<?>> getTypesToImport(JavaMethod method)
  {
    return new MethodGenerator(this, method).getTypesToImport();
  }
  
  Stream<Class<?>> getTypesToImport(JavaConstructor method)
  {
    return new ConstructorGenerator(this, method).getTypesToImport();
  }

  private void generateImport(String className)
  {
    printer.print("import ");
    printer.print(className);
    printer.print(";");
    printer.println();
  }

  private void generateClass()
  {
    generateJavaDoc();
    printer.print("public final class ");
    printer.print(clazz.getGeneratorSimpleName());
    new TypeVariablesDeclarationGenerator(imports, printer, clazz.getJava().getTypeParameters()).generate();
    printer.println();
    printer.print("{");
    printer.println();
    printer.indent(2);
    clazz.getFields().forEach(this::generate);
    if (!Modifier.isAbstract(clazz.getJava().getModifiers()))
    {
      clazz.getConstructors().forEach(this::generate);
    }
    clazz.getMethods().forEach(this::generate);
    generate$$$Method();
    printer.indent(0);
    printer.print("}");
    printer.println();
  }

  private void generateJavaDoc()
  {
    printer.print("/**");
    printer.println();
    printer.print(" * This source file was generated by WhatIsNewInJava.<br>");
    printer.println();
    printer.print(" *");
    printer.println();
    printer.print(" * This class provides an example call to each method in class {@link ");
    printer.print(clazz.getSimpleName());
    printer.print("}");
    printer.println();
    printer.print(" * that were newly introduced in");
    if (versions.isEmpty()) 
    {
      printer.print(" any");
    }
    printer.print(" Java ");
    printer.printPrural(versions, "version", "s");
    printer.print(' ');
    printer.forEachPrint(versions, ", ", version -> printer.print(version));
    printer.print(".<br>");
    printer.println();
    printer.print(" *");
    printer.println();
    printer.print(" * {@link ");
    printer.print(clazz.getSimpleName());
    if (versions.contains(clazz.getSince()))
    {
      printer.print("} is a completely new class.");
    }
    else
    {
      printer.print("} is an old class but has new fields, constructors or methods.");
    }
    printer.println();
    printer.print(" * @since ");
    printer.print(clazz.getSince());
    printer.println();
    printer.print(" * @see ");
    printer.print(clazz.getSimpleName());
    printer.println();
    printer.print(" */");
    printer.println();
  }

  void generate(JavaField field)
  {
    new FieldGenerator(this, field).generate();
  }
  
  void generate(JavaMethod method)
  {
    new MethodGenerator(this, method).generate();
  }

  void generate(JavaConstructor constructor)
  {
    new ConstructorGenerator(this, constructor).generate();
  }

  private void generate$$$Method()
  {
    if (!needsCreateMethod)
    {
      return;
    }
    printer.print("private ");
    new TesteeVariableTypeDeclarationGenerator(printer, clazz.getJava()).generate();
    printer.print(" $$$()");    
    printer.println();
    printer.print("{");
    printer.println();
    printer.indent(4);
    printer.print("return null;");
    printer.println();
    printer.indent(2);
    printer.print("}");
    printer.println();
  }

  Printer getPrinter()
  {
    return printer;
  }

  JavaClass getClazz()
  {
    return clazz;
  }

  Imports getImports()
  {
    return imports;
  }

  void needsCreateMethod()
  {
    needsCreateMethod = true;  
  }
}
