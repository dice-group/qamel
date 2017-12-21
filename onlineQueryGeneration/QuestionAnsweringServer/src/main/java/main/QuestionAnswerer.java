/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import AGDISTIS_Disambiguator.QuestionDisambiguator;
import SCNLP_Annotator.QuestionAnnotator;
import SPARQL_Executer.SparqlQueryExecuter;
import SPARQL_Generator.SparqlQueryGenerator;
import java.util.List;
import java.io.IOException;
import org.json.simple.parser.ParseException;

/**
 *
 * @author Florian
 */
public class QuestionAnswerer {

    private String questionString;
    private List<String> namedEntities;
    private List<String> entityURIs;
    private String generatedQuery;
    private String answer;

    public QuestionAnswerer() {
    }

    public String answerQuestion(String questionString) {
        this.questionString = questionString;
        findNamedEntities();
        disambiguateQuestion();
        generateQuery();
        executeQuery();

        return answer;
    }

    private void findNamedEntities() {
        // annotating the question (with Stanford Core NLP) to find the named entities
        QuestionAnnotator qa = new QuestionAnnotator();
        namedEntities = qa.findNamedEntities(questionString);
    }

    private void disambiguateQuestion() {
        // disambiguating the question (with AGDISTIS) to get the dbpedia URIs of the named entities
        QuestionDisambiguator disambiguator = new QuestionDisambiguator();
        try {
            entityURIs = disambiguator.disambiguateQuestion(questionString, namedEntities);
        } catch (ParseException | IOException ex) {
            System.out.println("Error");
        }
    }

    private void generateQuery() {
        // generating the query using the Patttern SPARQL Generator
        SparqlQueryGenerator generator = new SparqlQueryGenerator();
        generatedQuery = generator.generate(questionString, entityURIs);
        System.out.println(generatedQuery);
    }

    private void executeQuery() {
        // executing the query against the dbpedia SPARQL endpoint
        SparqlQueryExecuter executer = new SparqlQueryExecuter();
        answer = null;
        if (generatedQuery == "No pattern for those quantities of classes / properties / named entities available") {
            answer = "Answering the question wasn't possible";
        } else {
            try {
                answer = executer.execute(generatedQuery);
            } catch (IOException ex) {
            }
            System.out.println(answer);
        }
    }
}
