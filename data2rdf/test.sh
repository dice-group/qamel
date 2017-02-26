#!/bin/bash
echo "Testing recognition"
curl -H "Content-Type: application/json" -X POST -d '{"input": "The philosopher and mathematician Leibniz was born in Leipzig in 1646.","type":"text","lang":"en"}' http://localhost:8080/recognition
echo "Testing disambiguation"
curl -H "Content-Type: application/json" -X POST -d '{"input": "The philosopher and mathematician <entity>Leibniz</entity> was born in <entity>Leipzig</entity> in 1646.","type":"text","lang":"en"}' http://localhost:8080/disambiguation
echo "Testing text2K"
curl -H "Content-Type: application/json" -X POST -d '{ "input": "The philosopher and mathematician Leibniz was born in Leipzig in 1646.", "namedEntities": [{ "namedEntity": "Leibniz", "start": 35, "end": 42, "offset": 7, "disambiguatedURL": "http://dbpedia.org/resource/Gottfried_Wilhelm_Leibniz" }, { "namedEntity": "Leipzig", "start": 55, "end": 62, "offset": 7, "disambiguatedURL": "http://dbpedia.org/resource/Leipzig" }],"lang":"en"}' http://localhost:8080/text2k