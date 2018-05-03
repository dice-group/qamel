package org.dice.webservice;

import java.io.IOException;
import java.text.ParseException;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.servlet.http.HttpServletResponse;

import org.aksw.qa.commons.datastructure.Question;
import org.aksw.qa.commons.load.json.ExtendedQALDJSONLoader;
import org.dice.qa.MyQASystem;
import org.dice.qa.QASystem;
import org.dice.qa.impl.ExampleQASystem;
import org.dice.util.GerbilFinalResponse;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.github.jsonldjava.utils.JsonUtils;

@RestController
public class GerbilQABenchmarking {
	private Logger log = LoggerFactory.getLogger(GerbilQABenchmarking.class);
	protected QASystem system;
	
	
	public GerbilQABenchmarking() {
		/*
		 * This is an Example QA System providing a static response. 
		 * Implement your System as a QASystem and create it here
		 * 
		 * CREATE YOUR SYSTEM HERE 
		 */
		//system = new ExampleQASystem();
		system = new MyQASystem();
	}

	@RequestMapping(value = "/gerbil", method = RequestMethod.POST)
	public String askGerbil(@RequestParam Map<String, String> params, final HttpServletResponse response) throws ExecutionException, RuntimeException, IOException, ParseException {
		String question = params.get("query");
		String lang = params.get("lang");
		log.debug("Received question = " + question);
		log.debug("Language of question = " + lang);
		
		Question q = new Question();
		q.getLanguageToQuestion().put(lang, question);
		
		//call your QA system here 
		JSONObject answer = system.getAnswersToQuestion(q, lang);
		log.info("Got: "+JsonUtils.toPrettyString(answer));
		return JsonUtils.toPrettyString(answer);
	}

}