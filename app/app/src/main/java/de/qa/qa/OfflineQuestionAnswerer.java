package de.qa.qa;

import android.content.Context;

import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.MalformedQueryException;
import org.eclipse.rdf4j.query.TupleQueryResult;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.qa.qa.offline.Answer;
import de.qa.qa.offline.Match;
import de.qa.qa.result.FooterResult;
import de.qa.qa.result.HeaderResult;
import de.qa.qa.result.QAResult;
import de.qa.qa.result.TextResult;
import de.qa.qa.triplestore.TripleStore;
import info.debatty.java.stringsimilarity.Levenshtein;


public class OfflineQuestionAnswerer implements QuestionAnswerer {
    private static final String QUERY_PREFIX =
            "PREFIX rdfs:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                    "PREFIX rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                    "PREFIX  dbo: <http://dbpedia.org/ontology/> \n" +
                    "PREFIX  dbp: <http://dbpedia.org/property/> \n" +
                    "PREFIX  xsd: <http://www.w3.org/2001/XMLSchema#> \n";

    //Commonly used words without any useful information including verbs, articles, prepositions, pronouns and question words
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
    private List<Answer> mAnswers;
    private String mDatabasePath;

    public OfflineQuestionAnswerer(Context context) {
        mDatabasePath = new File(context.getExternalFilesDir(null), "/offline_data")
                .getAbsolutePath();
    }

    private void findMatches(String word) {
        try {
            word = word.replaceAll(" ", ".*").toLowerCase();
            String candidatesQuery = QUERY_PREFIX +
                    "SELECT DISTINCT ?x ?z WHERE { ?x <http://www.w3.org/2000/01/rdf-schema#label> ?z . FILTER regex(str(?x), \"(?i).*" + word + ".*\") FILTER (lang(?z)='en') }";
            TupleQueryResult result = TripleStore.query(mDatabasePath, candidatesQuery);
            while (result.hasNext()) {
                BindingSet set = result.next();
                String uri = set.getValue("x").stringValue();
                String label = set.getValue("z").stringValue();
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
        //Count occurrences as predicate
        query = QUERY_PREFIX +
                "SELECT (count (?x) as ?c) WHERE { ?x <" + uri + "> ?y }";
        occurrences[1] = Integer.parseInt(TripleStore.query(mDatabasePath, query).next()
                .getValue("c")
                .stringValue());
        //Count occurrences as object
        query = QUERY_PREFIX +
                "SELECT (count (?x) as ?c) WHERE { <" + uri + "> ?x ?y }";
        occurrences[2] = Integer.parseInt(TripleStore.query(mDatabasePath, query).next()
                .getValue("c")
                .stringValue());
        return occurrences;
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
        if (mQuestionType == 0) mQuestionType = QUESTION_TYPE_UNKNOWN;
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
        System.out.println(mQuestion);
        mWords = mQuestion.split(" ");
        for (String word : mWords) {
            if (word.equals(" ") || word.equals("")) continue;
            findMatches(word);
        }
        Collections.sort(mThings, new Match.Comparator());
        Collections.sort(mProperties, new Match.Comparator());
        return new QAResult[]{
                new HeaderResult(question),
                findBestAnswer(),
                new FooterResult(question)
        };
    }

    private QAResult findBestAnswer() {
        int maxConfidence = Integer.MIN_VALUE;
        for (Match thing : mThings) {
            StringBuilder queryBuilder = new StringBuilder();
            queryBuilder.append(QUERY_PREFIX).append("SELECT * WHERE {{}");
            if ((mQuestionType & QUESTION_TYPE_DATE) != 0) {
                queryBuilder.append("UNION { SELECT ?p ?o WHERE { <")
                        .append(thing.getUri())
                        .append("> ?p ?o . FILTER (datatype(?o) = xsd:date)}}");
                queryBuilder.append("UNION { SELECT ?p ?o WHERE { <")
                        .append(thing.getUri())
                        .append("> ?p ?o . FILTER (datatype(?o) = xsd:gYear)}}");
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
            }
            if ((mQuestionType & QUESTION_TYPE_PERSON) != 0) {
                queryBuilder.append("UNION { SELECT ?p ?o WHERE { <")
                        .append(thing.getUri())
                        .append("> ?p ?o . ?o rdf:type dbo:Person}}");
                queryBuilder.append("UNION { SELECT ?o ?p WHERE {?o ?p <")
                        .append(thing.getUri())
                        .append("> . ?o rdf:type dbo:Person}}");
            }
            if ((mQuestionType & QUESTION_TYPE_UNKNOWN) != 0) {
                queryBuilder.append("UNION { SELECT ?p ?o WHERE { <")
                        .append(thing.getUri())
                        .append("> ?p ?o .}}");
                queryBuilder.append("UNION { SELECT ?o ?p WHERE {?o ?p <")
                        .append(thing.getUri())
                        .append("> .}}");
            }
            queryBuilder.append("}");
            TupleQueryResult result = TripleStore.query(mDatabasePath, queryBuilder.toString());
            while (result.hasNext()) {
                maxConfidence = Math.max(evaluateResult(thing, result.next()), maxConfidence);
                if (maxConfidence >= -thing.getDistance()) {
                    Collections.sort(mAnswers, new Answer.Comparator());
                    return new TextResult(mQuestion, mAnswers.get(0).getAnswer());
                }
            }
        }
        Collections.sort(mAnswers, new Answer.Comparator());
        return new TextResult(mQuestion, mAnswers.get(0).getAnswer());
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
                int confidence = -1 * match.getDistance() + match.getWordsLength()
                        + match.getQuestion().length();
                mAnswers.add(new Answer(match, result, answer, mQuestion, confidence, propertyLabel));
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

    private String getLabel(String uri) {
        String query = "SELECT ?l WHERE { <" + uri + "> " +
                "<http://www.w3.org/2000/01/rdf-schema#label> ?l. FILTER (lang(?l='en'))}";
        TupleQueryResult labelResult = TripleStore.query(mDatabasePath, query);
        Value value;
        if (!labelResult.hasNext() || (value = labelResult.next().getValue("l")) == null)
            return null;
        return value.stringValue();
    }
}
