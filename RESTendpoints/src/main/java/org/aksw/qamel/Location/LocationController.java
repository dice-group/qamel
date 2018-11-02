package org.aksw.qamel.Location;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletResponse;

import org.aksw.qa.commons.datastructure.Question;
import org.aksw.qamel.OfflineQA.Answer;
import org.aksw.qamel.OfflineQA.FooterResult;
import org.aksw.qamel.OfflineQA.HeaderResult;
import org.aksw.qamel.OfflineQA.Match;
import org.aksw.qamel.OfflineQA.QAResult;
import org.aksw.qamel.OfflineQA.TextResult;
import org.aksw.qamel.RESTendpoints.Application;
import org.aksw.qamel.TeBaQA.TeBaQA;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.MalformedQueryException;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.jsonldjava.utils.JsonUtils;

import info.debatty.java.stringsimilarity.Levenshtein;

@RestController
@EnableAutoConfiguration
public class LocationController {
	private Logger log = LoggerFactory.getLogger(LocationController.class);

	protected TeBaQA tebaqa;

	public LocationController() {

		tebaqa = new TeBaQA();
	}

	// query=When was this church build?&lang=en&loc={"Kölner Dom", "Domplatte
	// Domvorplatz", "Römisches Nordtor"}
	@RequestMapping(value = "/location", method = RequestMethod.POST)
	public String askOffline(@RequestParam Map<String, String> params, final HttpServletResponse response)
			throws ExecutionException, RuntimeException, IOException, ParseException {
		String question = params.get("query");
		String lang = params.get("lang");
		String loc = params.get("loc");

		log.debug("Received question = " + question);
		log.debug("Language of question = " + lang);
		log.info("Locations = " + loc);

		// appending locations to questions since OQA should be able to handle that
		Pattern p = Pattern.compile("\"(\\w|ö|ä|ü|ß)+(\\s(\\w|ö|ä|ü|ß)*)*\"");
		Matcher m = p.matcher(loc);
		m.find();
		String locationString = m.group().replaceAll("\"", "");
		question = question.replace(" this ", " "+ locationString+" ");
		question = question.replace(" that ", " "+ locationString+" ");
		question = question.replace(" these ", " "+ locationString+" ");

		List<String> answer = tebaqa.answerQuestion(question);
		log.info("Got: " + JsonUtils.toPrettyString(answer));
		return JsonUtils.toPrettyString(answer);
	}

}