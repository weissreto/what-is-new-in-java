package ch.rweiss.whatisnew.java.type;

import java.util.List;
import java.util.function.Consumer;

public class Joiner
{
  public static <T> void forEach(T[] objects, Runnable between, Consumer<T> forEach)
  {
    boolean first = true;
    for (T object : objects)
    {
      if (!first)
      {
        between.run();
      }
      first = false;
      forEach.accept(object);
    }
  }

  public static <T> void forEach(List<T> objects, Runnable between, Consumer<T> forEach)
  {
    boolean first = true;
    for (T object : objects)
    {
      if (!first)
      {
        between.run();
      }
      first = false;
      forEach.accept(object);
    }
  }
}
