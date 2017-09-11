# RDF4A - Porting RDF4J to Android

A port of RDF4J to the Android platform.

## What is RDF4J?

> Eclipse RDF4J (formerly known as Sesame) is a powerful Java framework for processing and handling RDF data. This includes creating, parsing, scalable storage, reasoning and querying with RDF and Linked Data. It offers an easy-to-use API that can be connected to all leading RDF database solutions.

[RDF4J homepage](http://rdf4j.org/)

## What is RDF4A?

RDF4A is a modified version of RDF4J which can be used in Android apps. It is based on RDF4J 1.0.3. At the moment not everything has been tested so they may be some issues or instabilities.

## Setup

Install to local Maven repository
```
mvn  install -DskipTests
```

Include in an Android project
```
//build.gradle
repositories {
    mavenLocal()
}
dependencies {
    compile 'de.mm20.rdf4a:rdf4j-model:1.1-SNAPSHOT'
    [...]
}
```
