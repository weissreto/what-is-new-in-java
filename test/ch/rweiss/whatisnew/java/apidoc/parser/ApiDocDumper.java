package ch.rweiss.whatisnew.java.apidoc.parser;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

import ch.rweiss.whatisnew.java.WhatIsNewInException;
import ch.rweiss.whatisnew.java.apidoc.model.ApiClass;
import ch.rweiss.whatisnew.java.apidoc.model.ApiConstructor;
import ch.rweiss.whatisnew.java.apidoc.model.ApiDoc;
import ch.rweiss.whatisnew.java.apidoc.model.ApiField;
import ch.rweiss.whatisnew.java.apidoc.model.ApiMethod;

public class ApiDocDumper
{

  public static Path dumpApiDoc(ApiDoc apiDoc, Path path) throws IOException
  {
    Files.createDirectories(path.getParent());
    try (Writer writer = Files.newBufferedWriter(path))
    {
      apiDoc.getClasses().forEach(apiClass -> dumpClass(writer, apiClass));
    }
    return path;
  }

  private static void dumpClass(Writer writer, ApiClass apiClass)
  {
    try
    {
      writer.append(apiClass.toString());
      writer.append("\n");
      writer.append("  Fields:\n");
      apiClass.getFields().forEach(field -> dumpField(writer, field));
      writer.append("  Constructors:\n");
      apiClass.getConstructors().forEach(constructor -> dumpConstructor(writer, constructor));
      writer.append("  Methods:\n");
      apiClass.getMethods().forEach(method -> dumpMethod(writer, method));    
    }
    catch(IOException ex)
    {
      throw new WhatIsNewInException(ex);
    }
  }
  
  private static void dumpField(Writer writer, ApiField field) 
  {
    try
    {
      writer.append("    ");
      writer.append(field.toString());
      writer.append("\n");
    }
    catch(IOException ex)
    {
      throw new WhatIsNewInException(ex);
    }
  }
  
  private static void dumpConstructor(Writer writer, ApiConstructor constructor)
  {
    try
    {
      writer.append("    ");
      writer.append(constructor.toString());
      writer.append("\n");
    }
    catch(IOException ex)
    {
      throw new WhatIsNewInException(ex);
    }
  }

  private static void dumpMethod(Writer writer, ApiMethod method)
  {
    try
    {
      writer.append("    ");
      writer.append(method.toString());
      writer.append("\n");
    }
    catch(IOException ex)
    {
      throw new WhatIsNewInException(ex);
    }
  }

}
