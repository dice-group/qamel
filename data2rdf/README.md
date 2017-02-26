#Data2RDF Wrapper#

This is a wrapper project for the QAMEL server-side implementations. See also deliverable D2.1.

You can run this project using `mvn clean test spring-boot:run`

###Testing interface###
We provide three different interfaces

`curl -H "Content-Type: application/json" -X POST -d '{"input": "The philosopher and mathematician Leibniz was born in Leipzig in 1646.","type":"text","lang":"en"}' http://localhost:8080/recognition`

`curl -H "Content-Type: application/json" -X POST -d '{"input": "The philosopher and mathematician <entity>Leibniz</entity> was born in <entity>Leipzig</entity> in 1646.","type":"text","lang":"en"}' http://localhost:8080/disambiguation`


`curl -H "Content-Type: application/json" -X POST -d '{ "input": "The philosopher and mathematician Leibniz was born in Leipzig in 1646.", "namedEntities": [{ "namedEntity": "Leibniz", "start": 35, "end": 42, "offset": 7, "disambiguatedURL": "http://dbpedia.org/resource/Gottfried_Wilhelm_Leibniz" }, { "namedEntity": "Leipzig", "start": 55, "end": 62, "offset": 7, "disambiguatedURL": "http://dbpedia.org/resource/Leipzig" }],"lang":"en"}' http://localhost:8080/text2k`

###Issues###
 * Only POST interfaces?
 * How to choose other languages?
 * What does "type" mean in the input?
 * For the disambiguation it would be more senseful to give it a list of URIs
 * The datastructures for input and output are not really good reusable
 * The input for text2k contains one needless level (output) which could be circumvented by adding just an array under the top level