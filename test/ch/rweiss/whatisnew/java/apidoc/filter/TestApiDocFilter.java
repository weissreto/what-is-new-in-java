package ch.rweiss.whatisnew.java.apidoc.filter;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import ch.rweiss.whatisnew.java.apidoc.model.ApiDoc;
import ch.rweiss.whatisnew.java.apidoc.model.Version;
import ch.rweiss.whatisnew.java.apidoc.parser.ApiDocDumper;
import ch.rweiss.whatisnew.java.apidoc.parser.ApiDocParser;

public class TestApiDocFilter
{
  @Test
  public void filter9() throws IOException
  {
    filter("9");
  }
  
  @Test
  public void filter10() throws IOException
  {
    filter("10");
  }

  @Test
  public void filter11() throws IOException
  {
    filter("11");
  }

  @Test
  public void filter12() throws IOException
  {
    filter("12");
  }

  @Test
  public void filter13() throws IOException
  {
    filter("13");
  }
  
  @Test
  public void filter9_10_11() throws IOException
  {
    filter("9", "10", "11");
  }
  
  private void filter(String... filters) throws IOException
  {
    ApiDoc apiDoc = ApiDocParser.parse(Paths.get("target", "api-doc", "14", "docs"));
    List<Version> versions = Arrays.stream(filters).map(Version::valueOf).collect(Collectors.toList());
    ApiDoc filteredApiDoc = ApiDocFilter.filter(apiDoc, versions);
    String version = Arrays.stream(filters).collect(Collectors.joining("-"));
    Path path = ApiDocDumper.dumpApiDoc(filteredApiDoc, Paths.get("target", "api-doc-dumps", "java13-filtered-"+version+"-apidoc.txt"));
    assertThat(path).hasSameContentAs(Paths.get("testResources", "java13-filtered-"+version+"-apidoc.txt"));
  }
}
