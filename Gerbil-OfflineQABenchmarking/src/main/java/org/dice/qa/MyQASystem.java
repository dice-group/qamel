package org.dice.qa;

import java.util.HashSet;
import java.util.Set;

import javax.naming.Context;

import org.aksw.qamel.OfflineQABenchmark.App;
import org.dice.qa.AnswerContainer.AnswerType;

public class MyQASystem extends AbstractQASystem{
	private static final Context Context = null;
	 private App system = new App(Context);
	
//run with VM and Gerbil QA...................
	 @Override
		public AnswerContainer retrieveAnswers(String question, String lang) {
			//Create an empty container for your answers
			AnswerContainer answers = new AnswerContainer();
			
			//Create your answers as a Set
			Set<String> answerSet = new HashSet<String>();
			
			/**
			 * Here you have to actually ask your system for answers, the types and the sparql query
			 */
			system.answerQuestion(question);
			System.out.println("Question GERBIL: "+system.answerQuestion(question));
			answerSet.add(system.SingleAnswer);
			
			System.out.println("Binding Set: "+system.set);
			System.out.println("Single ans in Gerbil: " + system.SingleAnswer);
			//Set your answers
			answers.setAnswers(answerSet);
			System.out.println("Result Set-Gerbil: " + answerSet);
		
			

	                //Get the type (RESOURCE, BOOLEAN, NUMBER, DATE)
//	                AnswerType type = AnswerType.valueOf(system.lastAnswer().getType());
			answers.setType(AnswerType.RESOURCE);
			return answers;
		
	}



}
