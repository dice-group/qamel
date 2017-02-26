package org.aksw.qamel.data2rdf.controller;

import java.util.List;

import org.aksw.qamel.data2rdf.annotator.NED_AGDISTIS;
import org.aksw.qamel.data2rdf.datastructures.disambiguation.NEDAnnotation;
import org.aksw.qamel.data2rdf.datastructures.disambiguation.TextWithDisambiguatedEntities;
import org.aksw.qamel.data2rdf.datastructures.recognition.TextInput;
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
	public TextWithDisambiguatedEntities disambiguation(@RequestBody TextInput textInput) throws Exception {

		log.info("To disambiguate: " + textInput.toString());
		NED_AGDISTIS agdistis = new NED_AGDISTIS();

		String input = textInput.getInput();
		String lang = textInput.getLang();
		String type = textInput.getType();

		if (type.equals("text")) {
			List<NEDAnnotation> annotations = agdistis.runDisambiguation(input, lang);

			TextWithDisambiguatedEntities textWithRecognizedEntities = new TextWithDisambiguatedEntities(annotations);

			log.info("Disambiguated: " + textWithRecognizedEntities.toString());
			return textWithRecognizedEntities;
		} else if (type.equals("table")) {
			// This will be a job for TAIPAN by I. Ermilov
			throw new Exception("Disambiguation of entities in tables is not supported yet.");
		} else {
			throw new Exception("Unsupported type. Please specify 'text' or 'table',");
		}
	}
}
