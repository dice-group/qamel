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
public class NamedEntityRecognitionController {
Logger log = LoggerFactory.getLogger(NamedEntityRecognitionController.class);
	@RequestMapping(value = "/recognition", method = RequestMethod.POST)
	public TextWithRecognizedEntities greeting(@RequestBody TextInput input) {

		
		log.info("To recognize: "+input.toString());
		
		List<Annotation> annotations = new ArrayList<Annotation>();
		annotations.add(new Annotation(1, "Leibniz", new ArrayList<String>() {
			{
				add("scmsann:PERSON");
				add("ann:Annotation");
			}
		}, 34, 41));
		annotations.add(new Annotation(2, "Leipzig", new ArrayList<String>() {
			{
				add("scmsann:LOCATION");
				add("ann:Annotation");
			}
		}, 54, 61));
		
		TextWithRecognizedEntities textWithRecognizedEntities = new TextWithRecognizedEntities(annotations, new Context(null, null));
		log.info("Recognized: "+textWithRecognizedEntities.toString());
		return textWithRecognizedEntities;
	}
}
