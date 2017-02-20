package org.aksw.qamel.data2rdf.controller;

import java.util.ArrayList;
import java.util.List;

import org.aksw.qamel.data2rdf.datastructures.text2K.TextInputWithNamedEntities;
import org.aksw.qamel.data2rdf.datastructures.text2K.Triple;
import org.aksw.qamel.data2rdf.datastructures.text2K.Triples;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Text2KController {
	Logger log = LoggerFactory.getLogger(Text2KController.class);

	@RequestMapping(value = "/text2k", method = RequestMethod.POST)
	public Triples text2k(@RequestBody TextInputWithNamedEntities input) {

		log.info("To text2k: " + input.toString());

		List<Triple> tripleList = new ArrayList<Triple>();
		tripleList.add(new Triple("http://dbpedia.org/resource/Gottfried_Wilhelm_Leibniz", "http://dbpedia.org/ontology/birthPlace", "http://dbpedia.org/resource/Leipzig", 1.0));
		tripleList.add(new Triple("http://dbpedia.org/resource/Gottfried_Wilhelm_Leibniz", "http://dbpedia.org/ontology/homeTown", "http://dbpedia.org/resource/Leipzig", 1.0));
		tripleList.add(new Triple("http://dbpedia.org/resource/Gottfried_Wilhelm_Leibniz", "http://dbpedia.org/ontology/birthYear", "1646", 0.9));

		Triples triples = new Triples(tripleList);
		log.info("Text2k: " + triples.toString());
		return triples;
	}
}
