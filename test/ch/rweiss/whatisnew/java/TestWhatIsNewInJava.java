package ch.rweiss.whatisnew.java;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledOnJre;
import org.junit.jupiter.api.condition.EnabledOnJre;
import org.junit.jupiter.api.condition.JRE;

public class TestWhatIsNewInJava
{
  @Test
  public void generator9() throws Exception
  {
    generator("9");
    assertThat(countFiles("9")).isEqualTo(520L);
    assertThat(mvnCompile("9")).isEqualTo(0);
  }

  @Test
  public void generator10() throws Exception
  {
    generator("10");
    assertThat(countFiles("10")).isEqualTo(47L);
    assertThat(mvnCompile("10")).isEqualTo(0);
  }

  @Test
  public void generator11() throws Exception
  {
    generator("11");
    assertThat(countFiles("11")).isEqualTo(85L);
    assertThat(mvnCompile("11")).isEqualTo(0);
  }

  @Test
  public void generator12() throws Exception
  {
    generator("12");
    assertThat(countFiles("12")).isEqualTo(54L);
    assertThat(mvnCompile("12")).isEqualTo(0);
  }

  @Test
  @DisabledOnJre(JRE.JAVA_14)
  public void generator13() throws Exception
  {
    generator("13");
    assertThat(countFiles("13")).isEqualTo(28L);
    assertThat(mvnCompile("13")).isEqualTo(0);
  }

  @Test
  @EnabledOnJre(JRE.JAVA_14)
  public void generator14() throws Exception
  {
    generator("14");
    assertThat(countFiles("14")).isEqualTo(42L);
    assertThat(mvnCompile("14")).isEqualTo(0);
  }

  @Test
  public void generator9_10_11() throws Exception
  {
    generator("9", "10", "11");
    assertThat(countFiles("9", "10", "11")).isEqualTo(610L);
    assertThat(mvnCompile("9", "10", "11")).isEqualTo(0);
  }

  private void generator(String... filters) throws IOException
  {
    String version = toVersion(filters);
    String[] args = new String[filters.length+2];
    args[0] = Paths.get("target", "api-doc", "14", "docs").toString();
    args[1] = Paths.get("target", "what-is-new-in-java", version).toString();
    System.arraycopy(filters, 0, args, 2, filters.length);
    
    WhatIsNewInJava.main(args);
  }

  private Object countFiles(String ... filters) throws IOException
  {
    String version = toVersion(filters);
    
    Path outputPath = Paths.get("target", "what-is-new-in-java", version);
    return Files
        .walk(outputPath)
        .filter(path -> Files.isRegularFile(path))
        .count();        
  }
  
  private int mvnCompile(String... filters) throws IOException, InterruptedException
  {
    String version = toVersion(filters);
    Path outputPath = Paths.get("target", "what-is-new-in-java", version);

    return new ProcessBuilder(getMavenCommand(), "compile")
            .directory(outputPath.toFile())
            .inheritIO()
            .start()
            .waitFor(); 
  }

  private String getMavenCommand()
  {
    String mavenCommand = getMavenHome();
    if (SystemUtils.IS_OS_WINDOWS)
    {
      mavenCommand += File.separator + "mvn.cmd";
    }
    else
    {
      mavenCommand += File.separator + "mvn";
    }
    return mavenCommand;
  }

  private String getMavenHome()
  {
    String mavenHome = System.getenv("MAVEN_HOME");
    if (StringUtils.isNotBlank(mavenHome))
    {
      return mavenHome + File.separator + "bin";
    }
    mavenHome = System.getProperty("maven.home");
    if (StringUtils.isNotBlank(mavenHome))
    {
      return mavenHome + File.separator + "bin";
    }
    String path = System.getenv("PATH");
    String mavenPath = Arrays
        .stream(path.split(File.pathSeparator))
        .filter(pathEntry -> pathEntry.contains("maven"))
        .findAny()
        .orElse("C:\\Tools\\maven\\apache-maven-3.6.2\\bin");
    return mavenPath;
  }

  private String toVersion(String... filters)
  {
    return Arrays.stream(filters).collect(Collectors.joining("-"));
  }

}
