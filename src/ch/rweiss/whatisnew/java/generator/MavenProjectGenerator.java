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
    generateBuild();
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
    printer.print("    <maven.compiler.target>");
    generateVersion();
    printer.print("</maven.compiler.target>");
    printer.println();
    printer.print("    <maven.compiler.source>");
    generateVersion();
    printer.print("</maven.compiler.source>");
    printer.println();
    printer.print("  </properties>"); 
    printer.println();
    printer.println();
  }

  private void generateVersion()
  {
    if (versions.isEmpty())
    {
      printer.print(Runtime.version().feature());
    }
    else
    {
      printer.print(versions.get(versions.size()-1));
    }
  }

  private void generateBuild()
  {
    printer.print("  <build>");
    printer.println();
    printer.print("    <plugins>");
    printer.println();
    printer.print("      <plugin>");
    printer.println();
    printer.print("        <groupId>org.apache.maven.plugins</groupId>");
    printer.println();
    printer.print("        <artifactId>maven-compiler-plugin</artifactId>");
    printer.println();
    printer.print("        <version>3.8.0</version>");
    printer.println();
    printer.print("        <configuration>");
    printer.println();
    printer.print("          <compilerArgs>");
    printer.println();
    printer.print("            <arg>--add-exports</arg>");
    printer.println();
    printer.print("            <arg>jdk.jfr/jdk.jfr.internal=ALL-UNNAMED</arg>");
    printer.println();
    printer.print("          </compilerArgs>");
    printer.println();
    printer.print("        </configuration>");
    printer.println();
    printer.print("      </plugin>");
    printer.println();
    printer.print("    </plugins>");
    printer.println();
    printer.print("  </build>");
    printer.println();
    printer.println();
  }

  private void generateFooter()
  {
    printer.print("</project>");
    printer.println();
  }
}
