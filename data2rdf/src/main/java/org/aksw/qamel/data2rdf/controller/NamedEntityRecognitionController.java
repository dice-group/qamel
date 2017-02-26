package org.aksw.qamel.data2rdf.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.aksw.qamel.data2rdf.annotator.Entity;
import org.aksw.qamel.data2rdf.annotator.NER_FOX;
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

	@RequestMapping(value = "/recognition", method = RequestMethod.POST)
	public TextWithRecognizedEntities recognition(@RequestBody TextInput textInput) throws Exception {

		log.info("To recognize: " + textInput.toString());
		NER_FOX fox = new NER_FOX();

		List<NERAnnotation> annotations = new ArrayList<NERAnnotation>();

		String sentence = textInput.getInput();
		String lang = textInput.getLang();
		Map<String, List<Entity>> entities = fox.getEntities(sentence, lang);
		int id = 0;
		for (String key : entities.keySet()) {
			for (Entity entity : entities.get(key)) {

				ArrayList<String> listOfTypes = new ArrayList<String>();
				for (String r : entity.posTypesAndCategories) {
					listOfTypes.add(r);
				}

				annotations.add(new NERAnnotation(id++, entity.label, listOfTypes, entity.beginIndex, entity.endIndex));
			}
		}

		TextWithRecognizedEntities textWithRecognizedEntities = new TextWithRecognizedEntities(annotations, new Context(null, null));
		log.info("Recognized: " + textWithRecognizedEntities.toString());
		return textWithRecognizedEntities;
	}
}
