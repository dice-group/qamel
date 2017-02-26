#!/bin/bash

#######
##TODO TRANSFORM ME TO UNIT TEST
echo "Testing recognition (en)"
curl -H "Content-Type: application/json" -X POST -d '{"input": "The philosopher and mathematician Leibniz was born in Leipzig in 1646.","type":"text","lang":"en"}' http://localhost:8080/recognition
echo 
echo "Testing disambiguation (en)"
curl -H "Content-Type: application/json" -X POST -d '{"input": "The philosopher and mathematician <entity>Leibniz</entity> was born in <entity>Leipzig</entity> in 1646.","type":"text","lang":"en"}' http://localhost:8080/disambiguation
echo 
echo "Testing text2K (en)"
curl -H "Content-Type: application/json" -X POST -d '{ "input": "The philosopher and mathematician Leibniz was born in Leipzig in 1646.", "namedEntities": [{ "namedEntity": "Leibniz", "start": 35, "end": 42, "offset": 7, "disambiguatedURL": "http://dbpedia.org/resource/Gottfried_Wilhelm_Leibniz" }, { "namedEntity": "Leipzig", "start": 55, "end": 62, "offset": 7, "disambiguatedURL": "http://dbpedia.org/resource/Leipzig" }],"lang":"en"}' http://localhost:8080/text2k
echo 
echo "##################################"
echo 
echo "Testing recognition (de)"
curl -H "Content-Type: application/json" -X POST -d '{"input": "The philosopher and mathematician Leibniz was born in Leipzig in 1646 in Deutschland.","type":"text","lang":"de"}' http://localhost:8080/recognition
echo 
echo "Testing disambiguation (de)"
curl -H "Content-Type: application/json" -X POST -d '{"input": "The philosopher and mathematician <entity>Leibniz</entity> was born in <entity>Leipzig</entity> in 1646 in <entity>Deutschland</entity>.","type":"text","lang":"de"}' http://localhost:8080/disambiguation
echo 
echo "Testing text2K (de)"
curl -H "Content-Type: application/json" -X POST -d '{ "input": "The philosopher and mathematician Leibniz was born in Leipzig in 1646.", "namedEntities": [{ "namedEntity": "Leibniz", "start": 35, "end": 42, "offset": 7, "disambiguatedURL": "http://dbpedia.org/resource/Gottfried_Wilhelm_Leibniz" }, { "namedEntity": "Leipzig", "start": 55, "end": 62, "offset": 7, "disambiguatedURL": "http://dbpedia.org/resource/Leipzig" }],"lang":"de"}' http://localhost:8080/text2k
echo 
echo "##################################"
echo 
echo "Testing not yet supported table type recognition"
curl -H "Content-Type: application/json" -X POST -d '{"input": "The philosopher and mathematician Leibniz was born in Leipzig in 1646.","type":"table","lang":"en"}' http://localhost:8080/recognition
echo 
echo "Testing not yet supported table type disambiguation"
curl -H "Content-Type: application/json" -X POST -d '{"input": "The philosopher and mathematician <entity>Leibniz</entity> was born in <entity>Leipzig</entity> in 1646.","type":"table","lang":"en"}' http://localhost:8080/disambiguation
echo 
echo "Testing not yet supported table type recognition"
curl -H "Content-Type: application/json" -X POST -d '{"input": "The philosopher and mathematician Leibniz was born in Leipzig in 1646.","type":"foo","lang":"en"}' http://localhost:8080/recognition
echo 
echo "Testing not yet supported table type disambiguation"
curl -H "Content-Type: application/json" -X POST -d '{"input": "The philosopher and mathematician <entity>Leibniz</entity> was born in <entity>Leipzig</entity> in 1646.","type":"foo","lang":"en"}' http://localhost:8080/disambiguation
