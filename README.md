# What is new in Java [![Build Status](https://travis-ci.org/weissreto/what-is-new-in-java.svg?branch=master)](https://travis-ci.org/weissreto/what-is-new-in-java)

With the new release cadence of Java, that brings a new release every 6 month, it is hard for us as developer to keep up with learning all the new API that these new versions bring.

This library takes a Java API documentation (javadoc) and produces a java project with example classes for each of the new or modified Java API class for certain Java versions.

A developer can then use the generated project to learn what is new in the java API by doing the following steps:
1. Import the provided maven project into your favourite IDE.
2. Start browsing the provided classes and see what is new with the same tools that you are using in your daily work.

## GIT Repositories with pre-created projects

The following GIT repositories provide pre-generated projects for different Java versions:

* [What is new in Java 9](https://github.com/weissreto/what-is-new-in-java-9)
* [What is new in Java 10](https://github.com/weissreto/what-is-new-in-java-10)
* [What is new in Java 11](https://github.com/weissreto/what-is-new-in-java-11)
* [What is new in Java 9, 10 and 11](https://github.com/weissreto/what-is-new-in-java-9-10-11)
* [What is new in Java 12](https://github.com/weissreto/what-is-new-in-java-12)
* [What is new in Java 13](https://github.com/weissreto/what-is-new-in-java-13)
* [What is new in Java 14](https://github.com/weissreto/what-is-new-in-java-14)

## How to build the library

Ensure you have Java 13 and Maven (>= 3.6.2) installed on your machine. Ensure that the environment variable JAVA_HOME is set correctly to the installation directory of Java 13. Then simply use Maven to build the library:

```bash
mvn clean install
```

## How to use the library

```bash
java -jar what-is-new-in-java-0.1.0-SNAPSHOT-jar-with-dependencies.jar <path-to-java-api-doc> <output-path> <versions>
```

__Example__

The example below reads the Java API doc from the directory `api-doc` and generates a project into directory `java12and13` that contains example classes of all new API introduced in Java 12 and 13.
 
```bash
java -jar what-is-new-in-java-0.1.0-SNAPSHOT-jar-with-dependencies.jar api-doc java12and13 12 13
```

## Other resources

The following resources can also be used to learn what is new in Java API:

* [AdoptOpenJDK jdk-api-diff](https://github.com/AdoptOpenJDK/jdk-api-diff)
* [Java Almanac](https://github.com/marchof/java-almanac)

