package org.aksw.qamel.OQA.evaluation;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;

import org.aksw.qa.commons.datastructure.IQuestion;
import org.aksw.qa.commons.load.Dataset;
import org.aksw.qa.commons.load.LoaderController;
import org.aksw.qa.commons.measure.AnswerBasedEvaluation;
import org.aksw.qamel.OQA.OQA;
import org.aksw.qamel.OQA.QAResult;
import org.aksw.qamel.OQA.TextResult;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.sparql.core.TriplePath;
import org.apache.jena.sparql.syntax.ElementPathBlock;
import org.apache.jena.sparql.syntax.ElementVisitorBase;
import org.apache.jena.sparql.syntax.ElementWalker;

//TODO remove throws Exception
public class QALD7Benchmark {
	@SuppressWarnings("unused")
	public static void main(String[] args) throws Exception {
		OQA app = new OQA(new File("Database"));
		String newQuestion = null ;
		//		OQA app = new OQA("http://131.234.28.180:3030/ds/sparql");
		//		OQA app = new OQA("http://dbpedia.org/sparql");

		/*
		 * Scanner in = new Scanner(System.in); String ques = in.nextLine(); in.close();
		 */

		/*
		 * QAResult[] answerQuestion =
		 * app.answerQuestion("When was the Leipzig University founded?");
		 * System.out.println(((TextResult) answerQuestion[1]).getmData());
		 */

		List<IQuestion> questions = LoaderController.load(Dataset.QALD8_Train_Multilingual);
		int i = 0;
		double fmeasureavg = 0;
		for (IQuestion q : questions) {
			String Squery = q.getSparqlQuery();
			Query query = QueryFactory.create(Squery);
			final MyInt inte = new MyInt();
			if (!query.hasOffset() && !query.isAskType() && !query.hasGroupBy() && !query.hasHaving()
					&& !query.hasOrderBy()) {
				ElementWalker.walk(query.getQueryPattern(), new ElementVisitorBase() {
					@Override
					public void visit(ElementPathBlock block) {
						ListIterator<TriplePath> iterator = block.getPattern().iterator();
						while (iterator.hasNext()) {
							inte.tmp = inte.tmp + 1;
							TriplePath triplePath = iterator.next();
						}
					}
				});
			}
			if(inte.tmp==1) {
				newQuestion = q.getLanguageToQuestion().get("en");
				QAResult[] results = app.answerQuestion(newQuestion);
				System.out.println(++i + " / " + questions.size());
				System.out.println("answer: " + ((TextResult) results[0]).getmData());
				HashSet<String> systemAnswer = new HashSet<>(
				Arrays.asList(new String[] { ((TextResult) results[0]).getmData() }));
				double precision = AnswerBasedEvaluation.precision(systemAnswer, q);
				double recall = AnswerBasedEvaluation.recall(systemAnswer, q);
				double fMeasure = AnswerBasedEvaluation.fMeasure(systemAnswer, q);
				//System.out.println("BGP: " + inte.tmp);
				System.out.println("gold: "+ q.getGoldenAnswers().iterator().next());
				System.out.println("fmeasure: " + fMeasure);
				fmeasureavg += fMeasure;
			}
		}
		System.out.println(fmeasureavg / questions.size());
	}
}
class MyInt {
	public int tmp = 0;
}
