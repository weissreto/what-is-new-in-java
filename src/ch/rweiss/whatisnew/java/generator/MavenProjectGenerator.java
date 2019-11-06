package ch.rweiss.whatisnew.java.generator;

import java.util.List;

import ch.rweiss.whatisnew.java.model.Version;

final class MavenProjectGenerator
{
  private List<Version> versions;
  private Printer printer;

  MavenProjectGenerator(List<Version> versions, Printer printer)
  {
    this.versions = versions;
    this.printer = printer;
  }

  void generate()
  {
    generateHeader();
    generateCoordinates();
    generateProperties();
    generateFooter();
  }

  private void generateHeader()
  {
    printer.print("<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"");
    printer.println();
    printer.print("  xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\">"); 
    printer.println();
    printer.print("  <modelVersion>4.0.0</modelVersion>");
    printer.println();
    printer.println();
  }


  private void generateCoordinates()
  {
    printer.print("  <groupId>ch.rweiss</groupId>");
    printer.println();
    printer.print("  <artifactId>WhatIsNewInJavaVersion-");
    if (versions.isEmpty())
    {
      printer.print("any");
    }
    else
    {
      printer.forEachPrint(versions, "-", version -> printer.print(version));
    }
    printer.print("</artifactId>");
    printer.println();
    printer.print("  <version>0.0.1-SNAPSHOT</version>"); 
    printer.println();
    printer.print("  <packaging>jar</packaging>");
    printer.println();
    printer.println();
  }

  private void generateProperties()
  {
    printer.print("  <properties>"); 
    printer.println();
    printer.print("    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>"); 
    printer.println();
    printer.print("    <maven.compiler.release>");
    if (versions.isEmpty())
    {
      printer.print(Runtime.version().feature());
    }
    else
    {
      printer.print(versions.get(versions.size()-1));
    }
    printer.print("</maven.compiler.release>");
    printer.println();
    printer.print("  </properties>"); 
    printer.println();
  }

  private void generateFooter()
  {
    printer.print("</project>");
    printer.println();
  }
}
