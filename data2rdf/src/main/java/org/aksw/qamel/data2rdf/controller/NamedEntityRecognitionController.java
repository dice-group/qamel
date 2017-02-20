package org.aksw.qamel.data2rdf.controller;

import java.util.ArrayList;
import java.util.List;

import org.aksw.qamel.data2rdf.datastructures.recognition.Context;
import org.aksw.qamel.data2rdf.datastructures.recognition.NERAnnotation;
import org.aksw.qamel.data2rdf.datastructures.recognition.TextInput;
import org.aksw.qamel.data2rdf.datastructures.recognition.TextWithRecognizedEntities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NamedEntityRecognitionController {
	Logger log = LoggerFactory.getLogger(NamedEntityRecognitionController.class);

	@SuppressWarnings("serial")
	@RequestMapping(value = "/recognition", method = RequestMethod.POST)
	public TextWithRecognizedEntities recognition(@RequestBody TextInput input) {

		log.info("To recognize: " + input.toString());

		List<NERAnnotation> annotations = new ArrayList<NERAnnotation>();
		annotations.add(new NERAnnotation(1, "Leibniz", new ArrayList<String>() {
			{
				add("scmsann:PERSON");
				add("ann:Annotation");
			}
		}, 34, 41));
		annotations.add(new NERAnnotation(2, "Leipzig", new ArrayList<String>() {
			{
				add("scmsann:LOCATION");
				add("ann:Annotation");
			}
		}, 54, 61));

		TextWithRecognizedEntities textWithRecognizedEntities = new TextWithRecognizedEntities(annotations, new Context(null, null));
		log.info("Recognized: " + textWithRecognizedEntities.toString());
		return textWithRecognizedEntities;
	}
}
