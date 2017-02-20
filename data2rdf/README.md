curl -H "Content-Type: application/json" -X POST -d '{"input":"xyz","type":"xyz"}' http://localhost:8080/disambiguation

curl -H "Content-Type: application/json" -X POST -d '{ "input": "The philosopher and mathematician Leibniz was born in Leipzig in 1646.", "namedEntities": { "output": [ { "namedEntity": "Leibniz", "start": 35, "end": 42, "offset": 7, "disambiguatedURL": "http://dbpedia.org/resource/Gottfried_Wilhelm_Leibniz" }, { "namedEntity": "Leipzig", "start": 55, "end": 62, "offset": 7, "disambiguatedURL": "http://dbpedia.org/resource/Leipzig" } ] } }' http://localhost:8080/text2k

curl -H "Content-Type: application/json" -X POST -d '{"input":"xyz","type":"xyz"}' http://localhost:8080/recognition
