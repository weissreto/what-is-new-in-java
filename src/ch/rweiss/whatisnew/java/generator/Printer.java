package ch.rweiss.whatisnew.java.generator;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

import ch.rweiss.whatisnew.java.WhatIsNewInException;
import ch.rweiss.whatisnew.java.type.Joiner;

class Printer implements AutoCloseable
{
  private final BufferedWriter writer;
  private int indent;
  private boolean newLine = true;
  
  Printer(Path file)
  {
    try
    {
      writer = Files.newBufferedWriter(file);
    }
    catch(IOException ex)
    {
      throw new WhatIsNewInException(ex);
    }
  }
  
  void print(Object text)
  {
    try
    {   
      indent();      
      writer.append(text.toString());
      newLine = false;
    }
    catch(IOException ex)
    {
      throw new WhatIsNewInException(ex);
    }
  }
  
  void print(char character)
  {
    try
    {
      indent();      
      writer.append(character);
    }
    catch(IOException ex)
    {
      throw new WhatIsNewInException(ex);
    }
  }

  void println()
  {
    try
    {
      writer.append('\n');
      newLine = true;
    }
    catch(IOException ex)
    {
      throw new WhatIsNewInException(ex);
    }
  }

  void indent(int ind)
  {
    this.indent = ind;
  }

  Collector<String, Printer, Printer> toPrintedList(String delimiter)
  {
    return new PrintCollector(delimiter);
  }
  
  <T> void forEachPrint(T[] objects, String delimiter, Consumer<T> printer)
  {
    Joiner.forEach(objects, () -> print(delimiter), printer);
  }
  
  public <T> void forEachPrint(List<T> objects, String delimiter, Consumer<T> printer)
  {
    Joiner.forEach(objects, () -> print(delimiter), printer);
  }

  @Override
  public void close()
  {
    try
    {
      writer.close();
    }
    catch (IOException ex)
    {
      throw new WhatIsNewInException(ex);
    }
  }

  private void indent() throws IOException
  {
    if (newLine)
    {
      for (int pos = 0; pos < indent; pos++)
      {
        writer.append(' ');
      }
    }
    newLine = false;
  }

  private class PrintCollector implements Collector<String, Printer, Printer>
  {
    private String delimiter;
    private boolean first = true; 

    public PrintCollector(String delimiter)
    {
      this.delimiter = delimiter;
    }

    @Override
    public Supplier<Printer> supplier()
    {
      return () -> Printer.this;
    }

    @Override
    public BiConsumer<Printer, String> accumulator()
    {
      return (printer, text) -> {
        if (!first)
        {
          printer.print(delimiter);
        }
        first = false;
        printer.print(text);
      };
    }

    @Override
    public BinaryOperator<Printer> combiner()
    {
      return null;
    }

    @Override
    public Function<Printer, Printer> finisher()
    {
      return null;
    }

    @Override
    public Set<Characteristics> characteristics()
    {
      return EnumSet.of(Characteristics.IDENTITY_FINISH);
    }
  }
}
