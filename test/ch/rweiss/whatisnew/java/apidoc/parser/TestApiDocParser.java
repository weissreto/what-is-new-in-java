package ch.rweiss.whatisnew.java.apidoc.parser;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

import ch.rweiss.whatisnew.java.apidoc.model.ApiDoc;

public class TestApiDocParser
{
  @Test
  public void parseApiDoc13() throws IOException
  {
    ApiDoc apiDoc = ApiDocParser.parse(Paths.get("target", "api-doc", "13", "docs"));
    Path path = ApiDocDumper.dumpApiDoc(apiDoc, Paths.get("target", "api-doc-dumps", "java13-apidoc.txt"));
    assertThat(path).hasSameContentAs(Paths.get("testResources", "java13-apidoc.txt"));
  }
  
  @Test
  public void parseApiDoc14() throws IOException
  {
    ApiDoc apiDoc = ApiDocParser.parse(Paths.get("target", "api-doc", "14", "docs"));
    Path path = ApiDocDumper.dumpApiDoc(apiDoc, Paths.get("target", "api-doc-dumps", "java14-apidoc.txt"));
    assertThat(path).hasSameContentAs(Paths.get("testResources", "java14-apidoc.txt"));
  }

}
