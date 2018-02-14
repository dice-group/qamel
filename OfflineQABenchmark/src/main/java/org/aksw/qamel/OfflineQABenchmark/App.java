package org.aksw.qamel.OfflineQABenchmark;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import javax.naming.Context;

import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.MalformedQueryException;
import org.eclipse.rdf4j.query.TupleQueryResult;

import info.debatty.java.stringsimilarity.Levenshtein;
 
	public class App implements QuestionAnswerer {
		public String SingleAnswer;
		public BindingSet set;
		 public StringBuilder queryBuilder;
    private static final String QUERY_PREFIX =
            "PREFIX rdfs:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                    "PREFIX rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                    "PREFIX  dbo: <http://dbpedia.org/ontology/> \n" +
                    "PREFIX  dbp: <http://dbpedia.org/property/> \n" +
                    "PREFIX  xsd: <http://www.w3.org/2001/XMLSchema#> \n";
	private static final Context Context = null;

    public static void main(String [] args){
    	App app = new App(Context);
    
    	/*Scanner in = new Scanner(System.in);
    	System.out.println("Enter question: ");  
    	String ques = in.nextLine();
    	System.out.println(ques);   */ 
    	app.answerQuestion("What is the capital of France?"); 
    	 //in.close();  
    	    }

   

    private static final String[] BLACKLIST = {
            "the",
            "is",
            "are",
            "was",
            "were",
            "he",
            "she",
            "it",
            "they",
            "of",
            "in",
            "at",
            "by",
            "why",
            "who",
            "where",
            "when",
            "what",
            "how",
            "has",
            "have",
            "a"
    };
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
    private String mDatabasePath;
    

    public App(Context context) {
    	 mDatabasePath = new File("Database").getAbsolutePath();

    }

    private void findMatches(String word) {
        try {
            word = word.replaceAll(" ", ".*").toLowerCase();
            String candidatesQuery = QUERY_PREFIX +
                    "SELECT DISTINCT ?x ?z WHERE { ?x <http://www.w3.org/2000/01/rdf-schema#label> ?z . FILTER regex(str(?x), \"(?i).*" + word + ".*\") FILTER (lang(?z)='en') }";
            TupleQueryResult result = TripleStore.query(mDatabasePath, candidatesQuery);

            System.out.println("Tuple Query Result: "+result);
          
            System.out.println("Candidate Query: "+candidatesQuery);
            while (result.hasNext()) {
                System.out.println("Result: "+result);
                 set = result.next();
                System.out.println("Set: "+result);
                String uri = set.getValue("x").stringValue();
                String label = set.getValue("z").stringValue();
                System.out.println("Binding Set: "+set+"URI: "+uri+"Label: "+label);
                insertMatch(word, uri, label);
            }
        } catch (MalformedQueryException e) {
            System.err.println("Invalid query.");
            System.err.println(e.getLocalizedMessage());
        }
    }

    private void insertMatch(String word, String uri, String label) {
        Match match = new Match(uri, mQuestion, label, word, getOccurrences(uri));
        if (match.getType() == Match.TYPE_THING) {
            for (Match m : mThings) {
                if (m.getUri().equals(uri)) {
                    m.addWord(word);
                    System.out.println("Insert Match: "+match);
                    System.out.println("Match m: "+m);
                    return;
                }
            }
            mThings.add(match);
        } else {
            for (Match m : mProperties) {
                if (m.getUri().equals(uri)) {
                    m.addWord(word);
                    System.out.println(" Else Match: "+match);
                    return;
                }
            }
            mProperties.add(match);
        }
    }

    /**
     * Returns number of occurrences for each position of a statement
     *
     * @param uri the entity to lookup
     * @return an array containing<br>
     * [0] the number of occurrences as subject <br>
     * [1] the number of occurrences as predicate<br>
     * [2] the number of occurrences as object
     */
    private int[] getOccurrences(String uri) {
        int[] occurrences = new int[3];
        //Count occurrences as subject
        String query = QUERY_PREFIX +
                "SELECT (count (?x) as ?c) WHERE { <" + uri + "> ?x ?y }";
        occurrences[0] = Integer.parseInt(TripleStore.query(mDatabasePath, query).next()
                .getValue("c")
                .stringValue());
        System.out.println("Get Occurances Query (Subject): "+query);
        //Count occurrences as predicate
        query = QUERY_PREFIX +
                "SELECT (count (?x) as ?c) WHERE { ?x <" + uri + "> ?y }";
        occurrences[1] = Integer.parseInt(TripleStore.query(mDatabasePath, query).next()
                .getValue("c")
                .stringValue());
        System.out.println("Get Occurances Query (Predicate): "+query);
        //Count occurrences as object
        query = QUERY_PREFIX +
                "SELECT (count (?x) as ?c) WHERE { <" + uri + "> ?x ?y }";
        occurrences[2] = Integer.parseInt(TripleStore.query(mDatabasePath, query).next()
                .getValue("c")
                .stringValue());
        System.out.println("Get Occurances Query (Object): "+query);
        return occurrences;
    }

    private void determineQuestionType() {
        mQuestionType = 0;
        if (mQuestion.contains("when")) {
            mQuestionType |= QUESTION_TYPE_DATE;
            System.out.println("Question type"+mQuestionType);
        }
        if (mQuestion.contains("where")) {
            mQuestionType |= QUESTION_TYPE_PLACE;
            System.out.println("Question type: "+mQuestionType);
        }
        if (mQuestion.contains("who")) {
            mQuestionType |= QUESTION_TYPE_PERSON;
            System.out.println("Question type: "+mQuestionType);
        }
        if (mQuestion.contains("when")) {
            mQuestionType |= QUESTION_TYPE_DATE;
            System.out.println("Question type: "+mQuestionType);
        }
        if (mQuestion.contains("how many") || mQuestion.contains("how much")) {
            mQuestionType |= QUESTION_TYPE_NUMBER;
            System.out.println("Question type: "+mQuestionType);
        }
        if (mQuestionType == 0) mQuestionType = QUESTION_TYPE_UNKNOWN;
        System.out.println("Question type: "+mQuestionType);
    }

    @Override
    public QAResult[] answerQuestion(String question) {
        //Replace non alpha-numeric with spaces
        question = question.replaceAll("[^A-Za-z0-9\\s]", " ");
        //Remove redundant whitespaces ('    ' -> ' ')
        question = question.replaceAll("\\s+", " ");
        question = question.toLowerCase();
        mQuestion = question;
        mAnswers = new ArrayList<>();
        mThings = new ArrayList<>();
        mProperties = new ArrayList<>();
        determineQuestionType();
        //Add spaces at start and end of question
        mQuestion = " " + mQuestion + " ";
        for (String blacklisted : BLACKLIST) {
            mQuestion = mQuestion.replace(" " + blacklisted + " ", " ");
        }
        mQuestion = mQuestion.substring(1, mQuestion.length() - 1);
        System.out.println("Question replace: "+question);
        System.out.println("Things: "+mThings);
        System.out.println("Properties: "+mProperties);
        System.out.println("Question: "+mQuestion);
        mWords = mQuestion.split(" ");
        for (String word : mWords) {
            if (word.equals(" ") || word.equals("")) continue;
            findMatches(word);
            System.out.println("Words: "+mWords);
        }
        Collections.sort(mThings, new Match.Comparator());
        Collections.sort(mProperties, new Match.Comparator());
        HeaderResult headerResult = new HeaderResult(question);
		QAResult findBestAnswer = findBestAnswer();
		FooterResult footerResult = new FooterResult(question);
		return new QAResult[]{
                headerResult,
                findBestAnswer,
                footerResult
        };
    }

    public QAResult findBestAnswer() {
       
        int maxConfidence = Integer.MIN_VALUE;
        for (Match thing : mThings) {
            queryBuilder = new StringBuilder();
            queryBuilder.append(QUERY_PREFIX).append("SELECT * WHERE {{}");
            if ((mQuestionType & QUESTION_TYPE_DATE) != 0) {
                queryBuilder.append("UNION { SELECT ?p ?o WHERE { <")
                        .append(thing.getUri())
                        .append("> ?p ?o . FILTER (datatype(?o) = xsd:date)}}");
                queryBuilder.append("UNION { SELECT ?p ?o WHERE { <")
                        .append(thing.getUri())
                        .append("> ?p ?o . FILTER (datatype(?o) = xsd:gYear)}}");
                System.out.println("Which Query: "+queryBuilder);
                System.out.println("Match Thing: "+mThings);
            }
            if ((mQuestionType & QUESTION_TYPE_PLACE) != 0) {
                queryBuilder.append("UNION { SELECT ?p ?o WHERE { <")
                        .append(thing.getUri())
                        .append("> ?p ?o . FILTER (datatype(?o) = xsd:place)}}");
                queryBuilder.append("UNION { SELECT ?p ?o WHERE { <")
                        .append(thing.getUri())
                        .append("> ?p ?o . ?o rdf:type dbo:Place}}");
                queryBuilder.append("UNION { SELECT ?o ?p WHERE {?o ?p <")
                        .append(thing.getUri())
                        .append("> . ?s rdf:type dbo:Place}}");
                System.out.println("Where Query: "+queryBuilder);
            }
            if ((mQuestionType & QUESTION_TYPE_PERSON) != 0) {
                queryBuilder.append("UNION { SELECT ?p ?o WHERE { <")
                        .append(thing.getUri())
                        .append("> ?p ?o . ?o rdf:type dbo:Person}}");
                queryBuilder.append("UNION { SELECT ?o ?p WHERE {?o ?p <")
                        .append(thing.getUri())
                        .append("> . ?o rdf:type dbo:Person}}");
                System.out.println("Who Query: "+queryBuilder);
            }
            if ((mQuestionType & QUESTION_TYPE_UNKNOWN) != 0) {
                queryBuilder.append("UNION { SELECT ?p ?o WHERE { <")
                        .append(thing.getUri())
                        .append("> ?p ?o .}}");
                queryBuilder.append("UNION { SELECT ?o ?p WHERE {?o ?p <")
                        .append(thing.getUri())
                        .append("> .}}");
                System.out.println("Unknown Query: "+queryBuilder);
            }
            queryBuilder.append("}");
            TupleQueryResult result = TripleStore.query(mDatabasePath, queryBuilder.toString());
            while (result.hasNext()) {
                maxConfidence = Math.max(evaluateResult(thing, result.next()), maxConfidence);
                if (maxConfidence >= -thing.getDistance()) {
                    Collections.sort(mAnswers, new Answer.Comparator());
                    System.out.println("Answers: "+mAnswers);
                    System.out.println("MaxConfidence: "+maxConfidence);
                    return new TextResult(mQuestion, mAnswers.get(0).getAnswer());
                }
            }
        }
        Collections.sort(mAnswers, new Answer.Comparator());
        return new TextResult(mQuestion, "An error occurred");
    }

    private int evaluateResult(Match match, BindingSet result) {
        if (result.getValue("p") == null || result.getValue("o") == null) {
            return Integer.MIN_VALUE;
        }
        String property = result.getValue("p").stringValue();
        String propertyLabel = getLabel(property);
        for (Match p : mProperties) {
            if (p.getUri().equals(property)) {
                //TODO Save type
                String answer = result.getValue("o").stringValue();
                System.out.println("Evaluate result answer: "+answer);
                int confidence = -1 * match.getDistance() + match.getWordsLength()
                        + match.getQuestion().length();
                System.out.println("Evaluate result confidence: "+confidence);
                mAnswers.add(new Answer(match, result, answer, mQuestion, confidence, propertyLabel));
                SingleAnswer = answer;
                System.out.println("Single Answers: "+SingleAnswer);
                return confidence;
            }
        }
        if (propertyLabel == null) propertyLabel = "";
        int minDistance = Integer.MAX_VALUE;
        String word = "";
        for (String w : mWords) {
            if (w.equals(" ") || w.equals("")) continue;
            int distance = (int) new Levenshtein().distance(w, propertyLabel);
            if (minDistance > distance) {
                minDistance = distance;
                word = w;
            }
        }
        String answer = result.getValue("o").stringValue();
        int confidence = (int) (-2 * match.getDistance() - (10f * minDistance) / word.length()
                + match.getWordsLength());
        mAnswers.add(new Answer(match, result, answer, mQuestion, confidence, word));
        return confidence;
    }

    public String getLabel(String uri) {
        String query = "SELECT ?l WHERE { <" + uri + "> " +
                "<http://www.w3.org/2000/01/rdf-schema#label> ?l. FILTER (lang(?l='en'))}";
        TupleQueryResult labelResult = TripleStore.query(mDatabasePath, query);
        System.out.println("URI IRI: "+uri);
        System.out.println("get Label Query: "+query);
        System.out.println("Label result: "+labelResult);
        Value value;
       
        if (!labelResult.hasNext() || (value = labelResult.next().getValue("l")) == null)
        	
        return null;
        return value.stringValue();
    }
	}