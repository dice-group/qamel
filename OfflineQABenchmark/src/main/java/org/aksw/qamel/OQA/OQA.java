package org.aksw.qamel.OQA;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.aksw.qamel.OQA.sparql.SPARQLEndpoint;
import org.aksw.qamel.OQA.sparql.SPARQLInterface;
import org.aksw.qamel.OQA.sparql.TripleStore;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.MalformedQueryException;
import org.eclipse.rdf4j.query.QueryEvaluationException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import info.debatty.java.stringsimilarity.Levenshtein;

public class OQA {

	private static final String QUERY_PREFIX = "PREFIX rdfs:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
			+ "PREFIX rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
			+ "PREFIX  dbo: <http://dbpedia.org/ontology/> \n" + "PREFIX  dbp: <http://dbpedia.org/property/> \n"
			+ "PREFIX  xsd: <http://www.w3.org/2001/XMLSchema#> \n";
	private static final String[] BLACKLIST = { "the", "is", "did", "do", "his", "her", "to", "does", "are", "was",
			"were", "he", "she", "it", "they", "of", "in", "at", "by", "why", "who", "where", "when", "what", "which",
			"year", "how", "has", "have", "a", "all", "much", "many", "list", "give", "me", "with" };
	private static final int QUESTION_TYPE_DATE = 0x1;
	private static final int QUESTION_TYPE_PLACE = 0x2;
	private static final int QUESTION_TYPE_PERSON = 0x4;
	private static final int QUESTION_TYPE_NUMBER = 0x8;
	private static final int QUESTION_TYPE_UNKNOWN = 0x10;

	private String mQuestion;
	private String[] mWords;
	private List<Match> mThings;
	private List<Match> mProperties;
	private int mQuestionType;
	public List<Answer> mAnswers;
	private SPARQLInterface sparql;

	public OQA(File database) {
		String mDatabasePath = database.getAbsolutePath();
		sparql = new TripleStore(mDatabasePath);
	}

	public OQA(String SPARQLEndpoint) {
		sparql = new SPARQLEndpoint(SPARQLEndpoint);
	}

	private void findMatches(String word) throws Exception {
		try {
			word = word.replaceAll(" ", ".*").toLowerCase();
			String candidatesQuery = QUERY_PREFIX + "SELECT DISTINCT ?x ?z WHERE { "
					+ "?x <http://www.w3.org/2000/01/rdf-schema#label> ?z " + "FILTER regex(lcase(str(?x)), \"" + word
					+ "\") FILTER (lang(?z)='en' ) } " + "LIMIT 100";
			JSONArray result = sparql.query(candidatesQuery);
			checkBeforeInsertMatch(word, result);
		} catch (MalformedQueryException e) {
			System.err.println("Invalid query.");
			System.err.println(e.getLocalizedMessage());
		}
	}

	private void checkBeforeInsertMatch(String word, JSONArray result) {
		try {
			Iterator<org.json.simple.JSONObject> iter=result.iterator();
			while (iter.hasNext()) {
				JSONObject set = iter.next();
				String uri = (String) set.get("x");
				String label = (String) set.get("z");
				insertMatch(word, uri, label);
			}
		} catch (QueryEvaluationException ex) {
//			System.err.println("QueryEvaluationException.");
//			ex.printStackTrace();
		}
	}

	private void insertMatch(String word, String uri, String label) {
		Match match = new Match(uri, mQuestion, label, word);
		if (!uri.contains("/ontology/") && !uri.contains("/property/")) {
			for (Match m : mThings) {
				if (m.getUri().equals(uri)) {
					m.addWord(word);
					return;
				}
			}
			mThings.add(match);
		} else {
			for (Match m : mProperties) {
				if (m.getUri().equals(uri)) {
					m.addWord(word);
					return;
				}
			}
			mProperties.add(match);
		}
	}

	private void determineQuestionType() {
		mQuestionType = 0;
		if (mQuestion.contains("when")) {
			mQuestionType |= QUESTION_TYPE_DATE;
		}
		if (mQuestion.contains("where")) {
			mQuestionType |= QUESTION_TYPE_PLACE;
		}
		if (mQuestion.contains("who")) {
			mQuestionType |= QUESTION_TYPE_PERSON;
		}
		if (mQuestion.contains("when")) {
			mQuestionType |= QUESTION_TYPE_DATE;
		}
		if (mQuestion.contains("how many") || mQuestion.contains("how much")) {
			mQuestionType |= QUESTION_TYPE_NUMBER;
		}
		if (mQuestionType == 0)
			mQuestionType = QUESTION_TYPE_UNKNOWN;
	}

	public QAResult[] answerQuestion(String question) throws Exception {
		mQuestion = null;
		mWords = null;
		mThings = null;
		mProperties = null;
		mQuestionType = 0;
		mAnswers = null;
		System.out.println("******************************");

		System.out.println("Question original: " + question);

		// Replace non alpha-numeric with spaces, keep genitive 's, keep . for e.g. U.S.
		// remove trailing . for e.g. "List me... ."
		question = question.replaceAll("[^A-Za-z0-9'.\\s]", " ");
		if (question.endsWith(".")) {
			question = question.substring(0, question.lastIndexOf("."));
		}
		// Remove redundant whitespaces (' ' -> ' ')
		question = question.replaceAll("\\s+", " ");
		question = question.toLowerCase();
		mQuestion = question;
		mAnswers = new ArrayList<>();
		mThings = new ArrayList<>();
		mProperties = new ArrayList<>();
		determineQuestionType();
		// Add spaces at start and end of question
		mQuestion = " " + mQuestion + " ";
		for (String blacklisted : BLACKLIST) {
			mQuestion = mQuestion.replace(" " + blacklisted + " ", " ");
		}
		mQuestion = mQuestion.substring(1, mQuestion.length() - 1);
		mWords = mQuestion.split(" ");
		for (String word : mWords) {
			System.out.println(word);
			if (word.equals(" ") || word.equals(""))
				continue;
			findMatches(word);
		}
		Collections.sort(mThings, new Match.Comparator());
		Collections.sort(mProperties, new Match.Comparator());
		QAResult qaresult = findBestAnswer();
		return new QAResult[] { qaresult };
	}

	public QAResult findBestAnswer() throws Exception {

		int maxConfidence = Integer.MIN_VALUE;
		for (Match thing : mThings) {
			StringBuilder queryBuilder = new StringBuilder();
			queryBuilder.append(QUERY_PREFIX).append("SELECT DISTINCT * WHERE {{}");
			if ((mQuestionType & QUESTION_TYPE_DATE) != 0) {
				queryBuilder.append("UNION { SELECT ?p ?o WHERE { <").append(thing.getUri())
						.append("> ?p ?o .\n FILTER (datatype(?o) = xsd:date)}}");
				queryBuilder.append("UNION { SELECT ?p ?o WHERE { <").append(thing.getUri())
						.append("> ?p ?o .\n FILTER (datatype(?o) = xsd:gYear)}}");
			}
			if ((mQuestionType & QUESTION_TYPE_PLACE) != 0) {
				queryBuilder.append("UNION { SELECT ?p ?o WHERE { <").append(thing.getUri())
						.append("> ?p ?o .\n FILTER (datatype(?o) = xsd:place)}}");
				queryBuilder.append("UNION { SELECT ?p ?o WHERE { <").append(thing.getUri())
						.append("> ?p ?o .\n ?o rdf:type dbo:Place}}");
				queryBuilder.append("UNION { SELECT ?o ?p WHERE {?o ?p <").append(thing.getUri())
						.append("> .\n ?o rdf:type dbo:Place}}");
			}
			if ((mQuestionType & QUESTION_TYPE_PERSON) != 0) {
				queryBuilder.append("UNION { SELECT ?p ?o WHERE { <").append(thing.getUri())
						.append("> ?p ?o .\n ?o rdf:type dbo:Person}}");
				queryBuilder.append("UNION { SELECT ?o ?p WHERE {?o ?p <").append(thing.getUri())
						.append("> .\n ?o rdf:type dbo:Person}}");
			}
			if ((mQuestionType & QUESTION_TYPE_UNKNOWN) != 0) {
				queryBuilder.append("UNION { SELECT ?p ?o WHERE { <").append(thing.getUri()).append("> ?p ?o .}}");
				queryBuilder.append("UNION { SELECT ?o ?p WHERE {?o ?p <").append(thing.getUri()).append("> .}}");
			}
			queryBuilder.append("}");
			JSONArray result = sparql.query(queryBuilder.toString());
			Iterator<org.json.simple.JSONObject > iter=result.iterator();

			while (iter.hasNext()) {
				JSONObject next = iter.next();
				
				maxConfidence = Math.max(evaluateResult(thing, next), maxConfidence);
				if (maxConfidence >= -thing.getDistance()) {
					Collections.sort(mAnswers, new Answer.Comparator());
					return new TextResult(mQuestion, mAnswers.get(0).getAnswer());
				}
			}
		}
		Collections.sort(mAnswers, new Answer.Comparator());
		return new TextResult(mQuestion, "An error occurred");
	}

	private int evaluateResult(Match match, JSONObject next) throws Exception {
		if (next.get("p") == null || next.get("o") == null) {
			return Integer.MIN_VALUE;
		}
		String property = (String) next.get("p");
		String propertyLabel = getLabel(property);
		for (Match p : mProperties) {
			if (p.getUri().equals(property)) {
				// TODO Save type
				String answer =  (String) next.get("o");
				int confidence = -1 * match.getDistance() + match.getWordsLength() + match.getQuestion().length();
				mAnswers.add(new Answer(match, next, answer, mQuestion, confidence, propertyLabel));
				return confidence;
			}
		}
		if (propertyLabel == null)
			propertyLabel = "";
		int minDistance = Integer.MAX_VALUE;
		String word = "";
		for (String w : mWords) {
			if (w.equals(" ") || w.equals(""))
				continue;
			int distance = (int) new Levenshtein().distance(w, propertyLabel);
			if (minDistance > distance) {
				minDistance = distance;
				word = w;
			}
		}
		String answer = (String)  next.get("o");
		int confidence = (int) (-2 * match.getDistance() - (10f * minDistance) / word.length()
				+ match.getWordsLength());
		mAnswers.add(new Answer(match, next, answer, mQuestion, confidence, word));
		return confidence;
	}

	public String getLabel(String uri) throws Exception {
		String query = "SELECT ?l WHERE { <" + uri + "> "
				+ "<http://www.w3.org/2000/01/rdf-schema#label> ?l. FILTER (lang(?l='en'))}";
		JSONArray labelResult = sparql.query(query);
		Value value;
		Iterator<BindingSet> iter=labelResult.iterator();

		if (!iter.hasNext() || (value = iter.next().getValue("l")) == null)

			return null;
		return value.stringValue();
	}
}