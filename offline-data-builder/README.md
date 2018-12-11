# QAMEL Offline data builder

A tool to create offline data for the QAMEL app.

### How it works
The tool reads triples from an input file. For each triple it decides whether the triple contains relevant data or not. A triple is considered relevant if all three entities have at least n (a given number) occurrences in the base file (if the object of the triple is a literal: if the other two entites have that number of occurrences). A triple is stored if it is relevant or discarded if it is not. All relevant triples are stored to seperate n-triple file and later to a SailRepository (a kind of database type for RDF4J). Finally all database files are gzipped for distribution. Each offline data package gets a revision id (a hash of the included triples) to identify it. The tool also prints a JSONObject to use with the Offline-Data-Synchronizer.

### How to use
First you need an input file in n-triple format containing the base data. You can use a [DBpedia dump](http://wiki.dbpedia.org/downloads-2016-04) for that.
Now run
```
$ java -Xmx4g -jar dbbuilder.jar <input file> <threshold>
```
The Java heap size depends on the size of the input file. If you get an OutOfMemory exception, try to increase heap.

### Build
Run
```
$ mvn clean compile assembly:single
```
### Testing Questions for offline QA
Area of cologne/ What is the area of cologne
Famous bridge in cologne
What is the famous food in cologne
What is the name of cologne perfume/famous cologne perfume
number of animals in cologne zoo
Sports in cologne
Which beer brewed in cologne
what is the source of rhein
what is the length of rhein
inhabitants in cologne
Date of my meeting/What is the date of meeting
time of my deutsch unterricht
What is the time of my meeting
date of vacation