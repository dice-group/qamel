package org.dice.qa.impl;
import java.util.HashSet;
import java.util.Set;

import org.dice.qa.AbstractQASystem;
import org.dice.qa.AnswerContainer;
import org.dice.qa.AnswerContainer.AnswerType;


/**
 * A simple implementation of a QASystem which responses with a static AnswerContainer Object
 *
 */
public class ExampleQASystem extends AbstractQASystem{

	@Override
	public AnswerContainer retrieveAnswers(String question, String lang) {
		//Create your answer container
		AnswerContainer answers = new AnswerContainer();
		
		//Create your answers
		Set<String> answerSet = new HashSet<String>();
		/**
		 * Here you have to actually ask your system for answers, the types and the sparql query
		 */
		answerSet.add("http://example.com/res/static-1");
		answerSet.add("http://example.com/res/static-2");
		answerSet.add("http://example.com/res/static-3");
		
		//Set your answers
		answers.setAnswers(answerSet);
		answers.setType(AnswerType.RESOURCE);
		answers.setSparqlQuery("SELECT ?s {?s ?p <http://example.com/prop/staticClass>} LIMIT 3");

		return answers;
	}
	

}
