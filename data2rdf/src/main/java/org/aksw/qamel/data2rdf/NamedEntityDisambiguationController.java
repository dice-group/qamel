package org.aksw.qamel.data2rdf;

import java.util.ArrayList;
import java.util.List;

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
	public TextWithDisambiguatedEntities greeting(@RequestBody TextInput input) {

		
		log.info("To disambiguate: "+input.toString());
		
		List<NEDAnnotation> annotations = new ArrayList<NEDAnnotation>();
		annotations.add(new NEDAnnotation("Leibniz", 35,42,7,"http://dbpedia.org/resource/Gottfried_Wilhelm_Leibniz"));
		annotations.add(new NEDAnnotation("Leipzig", 55,62,7,"http://dbpedia.org/resource/Leipzig"));
		
		
		TextWithDisambiguatedEntities textWithRecognizedEntities = new TextWithDisambiguatedEntities(annotations);
		log.info("Disambiguated: "+textWithRecognizedEntities.toString());
		return textWithRecognizedEntities;
	}
}
