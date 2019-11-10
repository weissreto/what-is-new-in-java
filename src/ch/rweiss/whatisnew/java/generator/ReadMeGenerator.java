package ch.rweiss.whatisnew.java.generator;

import java.util.List;
import java.util.stream.Collectors;

import ch.rweiss.whatisnew.java.apidoc.model.Version;

final class ReadMeGenerator
{
  private final String versions;
  private final Printer printer;

  public ReadMeGenerator(List<Version> versions, Printer printer)
  {
    this.versions = toUserFriendlyVersionsString(versions);
    this.printer = printer;
  }

  private static String toUserFriendlyVersionsString(List<Version> versions)
  {
    StringBuilder builder = new StringBuilder();
    if (versions.size() >= 2)
    {
      builder.append(versions
          .subList(0, versions.size()-1)
          .stream()
          .map(Version::toString)
          .collect(Collectors.joining(", ")));
      builder.append(" and ");
      builder.append(versions.get(versions.size()-1).toString());
    }
    else if (versions.size() == 1)
    {
      builder.append(versions.get(0).toString());
    }
    return builder.toString();
  }

  public void generate()
  {
    generateTitle();
    generateDescription();
  }

  private void generateTitle()
  {
    printer.print("# What is new in Java");
    printer.print(' ');
    printer.print(versions);
    printer.println();
    printer.println();
  }

  private void generateDescription()
  {
    printer.print("With the new release cadence of Java, that brings a new release every 6 month, ");
    printer.print("it is hard for us as developer to keep up with learning all the new API that these new versions bring.");
    printer.println();
    printer.println();
    printer.print("This repository should help you to find out and learn what new API Java ");
    printer.print(versions);
    printer.print(" have. It does that by providing you example classes for each of the new or modified Java API class.");
    printer.println();
    printer.println();
    printer.print("To learn what is new in Java ");
    printer.print(versions);
    printer.print(" you can do the following steps:");
    printer.println();
    printer.print("1. Clone this repository to your local machine.");
    printer.println();
    printer.print("2. Import the provided maven project into your favourite IDE.");
    printer.println();
    printer.print("3. Start browsing the provided classes and see what is new with the same tools that you are using in your daily work.");
  }
}
