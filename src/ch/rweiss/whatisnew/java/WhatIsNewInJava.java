package ch.rweiss.whatisnew.java;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import ch.rweiss.whatisnew.java.filter.ApiDocFilter;
import ch.rweiss.whatisnew.java.generator.Generator;
import ch.rweiss.whatisnew.java.model.ApiDoc;
import ch.rweiss.whatisnew.java.model.Version;
import ch.rweiss.whatisnew.java.parser.ApiDocParser;

public class WhatIsNewInJava
{
  public static void main(String[] args) throws IOException
  {
    Path root = Paths.get(args[0]);
    Path outputPath = Paths.get("../Generated/src");
    Directory.delete(outputPath);
    ApiDoc apiDoc = ApiDocParser.parse(root);
    apiDoc = ApiDocFilter.filter(apiDoc, Version.valueOf("11"));
    new Generator(apiDoc, outputPath).generate();
  }
}
