package org.aksw.qamel.OQA.evaluation;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.aksw.qa.commons.datastructure.IQuestion;
import org.aksw.qa.commons.load.Dataset;
import org.aksw.qa.commons.load.LoaderController;
import org.aksw.qa.commons.measure.AnswerBasedEvaluation;
import org.aksw.qamel.OQA.OQA;
import org.aksw.qamel.OQA.QAResult;
import org.aksw.qamel.OQA.TextResult;

public class QALD7Benchmark {
	@SuppressWarnings("unused")
	public static void main(String[] args) {
		OQA app = new OQA(new File("Database"));
//		OQA app = new OQA("http://dbpedia.org/sparql");
		/*
		 * Scanner in = new Scanner(System.in); String ques = in.nextLine(); in.close();
		 */

		/*
		 * QAResult[] answerQuestion =
		 * app.answerQuestion("When was the Leipzig University founded?");
		 * System.out.println(((TextResult) answerQuestion[1]).getmData());
		 */

		List<IQuestion> questions = LoaderController.load(Dataset.QALD7_Train_Multilingual);
		int i = 0;
		double fmeasureavg = 0;
		for (IQuestion q : questions) {

			String question = q.getLanguageToQuestion().get("en");
			QAResult[] results = app.answerQuestion(question);
			
			System.out.println(++i + " / " + questions.size());
			System.out.println("answer: " + ((TextResult) results[0]).getmData());
			
			HashSet<String> systemAnswer = new HashSet<>(
					Arrays.asList(new String[] { ((TextResult) results[0]).getmData() }));
			double precision = AnswerBasedEvaluation.precision(systemAnswer, q);
			double recall = AnswerBasedEvaluation.recall(systemAnswer, q);
			double fMeasure = AnswerBasedEvaluation.fMeasure(systemAnswer, q);
			fmeasureavg += fMeasure;
			
		}
		
		System.out.println(fmeasureavg / questions.size());
	}
}
