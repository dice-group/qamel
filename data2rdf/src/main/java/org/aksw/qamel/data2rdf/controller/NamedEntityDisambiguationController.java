package org.aksw.qamel.data2rdf.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.aksw.qamel.data2rdf.annotator.NED_AGDISTIS;
import org.aksw.qamel.data2rdf.datastructures.disambiguation.NEDAnnotation;
import org.aksw.qamel.data2rdf.datastructures.disambiguation.TextWithDisambiguatedEntities;
import org.aksw.qamel.data2rdf.datastructures.recognition.TextInput;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NamedEntityDisambiguationController {
	Logger log = LoggerFactory.getLogger(NamedEntityDisambiguationController.class);

	@RequestMapping(value = "/disambiguation", method = RequestMethod.POST)
	public TextWithDisambiguatedEntities disambiguation(@RequestBody TextInput input) throws ParseException, IOException {

		log.info("To disambiguate: " + input.toString());
		NED_AGDISTIS agdistis = new NED_AGDISTIS();

		List<NEDAnnotation> annotations = agdistis.runDisambiguation(input.getInput());

		TextWithDisambiguatedEntities textWithRecognizedEntities = new TextWithDisambiguatedEntities(annotations);

		log.info("Disambiguated: " + textWithRecognizedEntities.toString());
		return textWithRecognizedEntities;
	}
}
