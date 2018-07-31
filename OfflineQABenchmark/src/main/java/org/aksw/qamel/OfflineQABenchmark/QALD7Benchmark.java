package org.aksw.qamel.OfflineQABenchmark;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import javax.naming.Context;

import org.aksw.qa.commons.datastructure.IQuestion;
import org.aksw.qa.commons.load.Dataset;
import org.aksw.qa.commons.load.LoaderController;
import org.aksw.qa.commons.measure.AnswerBasedEvaluation;

public class QALD7Benchmark {

	private static final Context Context = null;

	public static void main(String[] args) {
		App app = new App(Context);

		/*
		 * Scanner in = new Scanner(System.in); String ques = in.nextLine(); in.close();
		 */

//		QAResult[] answerQuestion = app.answerQuestion("When was the Leipzig University founded?");
//		System.out.println(((TextResult) answerQuestion[1]).getmData());
//		 answerQuestion = app.answerQuestion("What is the capital of Germany?");
//		System.out.println(((TextResult) answerQuestion[1]).getmData());
		List<IQuestion> questions = LoaderController.load(Dataset.QALD7_Train_Multilingual);
		int i = 0;
		double fmeasureavg = 0;
		for (IQuestion q : questions) {
			System.out.println(++i + " / " + questions.size());
			String question = q.getLanguageToQuestion().get("en");
			QAResult[] results = app.answerQuestion(question);
			System.out.println("answer: " + ((TextResult) results[1]).getmData());
			HashSet<String> systemAnswer = new HashSet<>(
					Arrays.asList(new String[] { ((TextResult) results[1]).getmData() }));
			double precision = AnswerBasedEvaluation.precision(systemAnswer, q);
			double recall = AnswerBasedEvaluation.recall(systemAnswer, q);
			double fMeasure = AnswerBasedEvaluation.fMeasure(systemAnswer, q);
			fmeasureavg += fMeasure;
		}
		System.out.println(fmeasureavg / i);
	}
}
