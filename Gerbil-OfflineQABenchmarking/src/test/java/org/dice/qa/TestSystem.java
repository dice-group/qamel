package org.dice.qa;

import java.util.HashSet;
import java.util.Set;

import org.dice.qa.AnswerContainer.AnswerType;

/**
 * Test System for all answer types.<br/><br/>
 * Mapping is as follows:<br/>
 * how old - Number<br/>
 * when - date<br/>
 * is - boolean<br/>
 * what - string<br/>
 * who - resource/uri<br/>
 * nothing of the above - empty<br/>
 *
 */
public class TestSystem extends AbstractQASystem {

	@Override
	public AnswerContainer retrieveAnswers(String question, String lang) {
		AnswerContainer container = new AnswerContainer();
		String cleanQuestion = question.trim().toLowerCase();
		container.setSparqlQuery("SELECT ?o {?s ?p ?o}");
		Set<String> answers = new HashSet<String>();
		if(cleanQuestion.startsWith("how old")) {
			container.setType(AnswerType.NUMBER);
			answers.add("123");
			answers.add("\"23.0\"^^<http://www.w3.org/2001/XMLSchema#double>");
		}
		else if(cleanQuestion.startsWith("when")) {
			container.setType(AnswerType.DATE);
			answers.add("\"2002-09-24\"^^<http://www.w3.org/2001/XMLSchema#date>");
		}
		else if(cleanQuestion.startsWith("is")) {
			container.setType(AnswerType.BOOLEAN);
			answers.add("true");
		}
		else if(cleanQuestion.startsWith("what")) {
			container.setType(AnswerType.STRING);
			answers.add("\"Hawaii\"@en");
		}
		else if(cleanQuestion.startsWith("where")) {
			container.setType(AnswerType.RESOURCE);
			answers.add("http://dbpedia.org/resource/Hawaii");
			answers.add("http://dbpedia.org/resource/New_York");
		}
		else {
			//empty test
			container.setSparqlQuery("");
			container.setType(AnswerType.RESOURCE);
		}
		container.setAnswers(answers);
		return container;
	}

}
