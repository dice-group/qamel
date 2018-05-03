package org.dice.qa;

import static org.junit.Assert.assertEquals;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.aksw.qa.commons.datastructure.Question;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Test;

/**
 * Tests the JSON functionality of the AbstractQASystem
 * using a simple TestSystem
 *
 */
public class JsonTest {

	/**
	 * Checks if the JSON provided by the AbstractQASystem is valid
	 * @throws IOException
	 * @throws ParseException
	 */
	@Test
	public void checkValidity() throws IOException, ParseException {
		TestSystem system = new TestSystem();
		
		JSONObject obj = (JSONObject) new JSONParser().parse(new FileReader("src/test/resources/qald_test/qald.json"));
		JSONArray questions = (JSONArray) obj.get("questions");
		for(Object qObj : questions) {
			JSONObject question = (JSONObject) qObj;
			Question q = new Question();
			
			HashMap<String, String> mapping = new HashMap<String, String>();
			mapping.put("en", ((JSONObject)((JSONArray)question.get("question")).get(0)).get("string").toString());
			q.setLanguageToQuestion(mapping);
			JSONObject answer = system.getAnswersToQuestion(q, "en");
			
			Set<String> goldenSet = new HashSet<String>();
			createSet(goldenSet, question);
			
			Set<String> answerSet = new HashSet<String>();
			createSet(answerSet, ((JSONObject)((JSONArray)answer.get("questions")).get(0)));
			
			assertEquals(goldenSet, answerSet);
		}
	}
	
	private void createSet(Set<String> set, JSONObject qald) {
		String answerType = qald.get("answertype").toString();
		if(answerType.equals("boolean")) {
			set.add(((JSONObject)((JSONArray)qald.get("answers")).get(0)).get("boolean").toString());
			return;
		}
		JSONArray answers = (JSONArray) qald.get("answers");
		for(Object answerObj : answers) {
			JSONObject answer = (JSONObject) answerObj;
			
			String varName = ((JSONArray)((JSONObject)answer.get("head")).get("vars")).get(0).toString();
			JSONArray bindings = ((JSONArray)((JSONObject)answer.get("results")).get("bindings"));
			for(Object bindingObj : bindings) {
				JSONObject binding = (JSONObject) bindingObj;
				JSONObject varBind = (JSONObject)binding.get(varName);
				StringBuilder value = new StringBuilder(varBind.get("value").toString());
				if(varBind.containsKey("xml:lang")) {
					//has language tag
					value.insert(0, "\"");
					value.append("\"@").append(varBind.get("xml:lang"));
				}
				else if(varBind.containsKey("datatype")) {
					//has datatype key
					value.insert(0, "\"");
					value.append("\"^^<").append(varBind.get("datatype")).append(">");
				}
				set.add(value.toString());
			}
		}
	}
}
