package ch.rweiss.whatisnew.java;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import ch.rweiss.whatisnew.java.apidoc.filter.ApiDocFilter;
import ch.rweiss.whatisnew.java.apidoc.model.ApiDoc;
import ch.rweiss.whatisnew.java.apidoc.model.Version;
import ch.rweiss.whatisnew.java.apidoc.parser.ApiDocParser;
import ch.rweiss.whatisnew.java.generator.Generator;
import ch.rweiss.whatisnew.java.generator.model.JavaApi;
import ch.rweiss.whatisnew.java.resolver.JavaResolver;

public class WhatIsNewInJava
{
  private List<Version> filterArgs = new ArrayList<>();
  private String outputPathArg;
  private String apiDocRootPathArg;
//  private FileHandler handler;
//  private Logger logger;

  public WhatIsNewInJava(String[] args)
  {
    if (args.length < 2)
    {
      return;
    }
    apiDocRootPathArg = args[0];
    outputPathArg = args[1];
    for (int pos = 2; pos < args.length; pos++)
    {
      filterArgs.add(Version.valueOf(args[pos]));
    }
//    logger = Logger.getLogger("ClassFileParser");
//    logger.setLevel(Level.FINE);
//    handler = new FileHandler("log.log");
//    handler.setLevel(Level.FINE);
//    handler.setFormatter(new SimpleFormatter());
//    logger.addHandler(handler);
  }

  public static void main(String[] args) throws IOException
  {
    try
    {
      WhatIsNewInJava tool = new WhatIsNewInJava(args);
      tool.generate();
    }
    catch(WhatIsNewInException ex)
    {
      ex.printStackTrace();
    }    
  }

  private void generate() throws IOException
  {
    if (apiDocRootPathArg == null || outputPathArg == null)
    {
      printHelp();
      return;
    }
    Path root = Paths.get(apiDocRootPathArg);
    Path outputPath = Paths.get(outputPathArg);
    Directory.delete(outputPath);
    ApiDoc apiDoc = ApiDocParser.parse(root);
    apiDoc = ApiDocFilter.filter(apiDoc, filterArgs);
    JavaApi javaApi = new JavaResolver(apiDoc).toJavaApi();
    new Generator(javaApi, filterArgs, outputPath).generate();
  }

  private static void printHelp()
  {
    System.err.println("No api doc root path or output path was given");
    System.err.println("Usage: WhatIsNewInJava apiDocRootPath outputPath [version, ...]");
  }
}
