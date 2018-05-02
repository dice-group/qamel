#Data2RDF Wrapper#

This is a wrapper project for the QAMEL server-side implementations. See also deliverable D2.1.

You can run this project using `mvn clean test spring-boot:run`

###Testing interface###
We provide three different interfaces

`curl -H "Content-Type:application/json;charset=utf-8" -X POST -d '{"input": "The philosopher and mathematician Leibniz was born in Leipzig in 1646.","type":"text","lang":"en", "task":"ner"}' http://localhost:8080/recognition`

`curl -H "Content-Type:application/json" -X POST -d '{"input": "The philosopher and mathematician <entity>Leibniz</entity> was born in <entity>Leipzig</entity> in 1646.","type":"text","lang":"en"}' http://localhost:8080/disambiguation`


`curl -H "Content-Type:application/json" -X POST -d '{ "input": "The philosopher and mathematician Leibniz was born in Leipzig in 1646.", "namedEntities": [{ "namedEntity": "Leibniz", "start": 35, "end": 42, "offset": 7, "disambiguatedURL": "http://dbpedia.org/resource/Gottfried_Wilhelm_Leibniz" }, { "namedEntity": "Leipzig", "start": 55, "end": 62, "offset": 7, "disambiguatedURL": "http://dbpedia.org/resource/Leipzig" }],"lang":"en"}' http://localhost:8080/text2k`
