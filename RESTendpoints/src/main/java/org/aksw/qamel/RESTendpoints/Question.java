package org.aksw.qamel.RESTendpoints;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

public class Question {
	/*Logger log = LoggerFactory.getLogger(Question.class);

	@RequestMapping(value = "/disambiguation", method = RequestMethod.POST)
//	public TextWithDisambiguatedEntities disambiguation(@RequestBody TextInput textInput) throws Exception {

		log.info("To disambiguate: " + textInput.toString());
		App agdistis = new App();

		String input = textInput.getInput();
		String lang = textInput.getLang();
		String type = textInput.getType();

		if (type.equals("text")) {
		//	List<NEDAnnotation> annotations = agdistis.runDisambiguation(input, lang);

			//TextWithDisambiguatedEntities textWithRecognizedEntities = new TextWithDisambiguatedEntities(annotations);

		//	log.info("Disambiguated: " + textWithRecognizedEntities.toString());
			//return textWithRecognizedEntities;
		} else if (type.equals("table")) {
			// This will be a job for TAIPAN by I. Ermilov
			throw new Exception("Disambiguation of entities in tables is not supported yet.");
		} else {
			throw new Exception("Unsupported type. Please specify 'text' or 'table',");
		}
	}*/

}
