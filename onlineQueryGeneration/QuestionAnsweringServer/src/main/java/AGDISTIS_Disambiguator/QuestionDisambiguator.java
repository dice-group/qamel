/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AGDISTIS_Disambiguator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.json.simple.parser.ParseException;

/**
 *
 * @author Florian
 */
public class QuestionDisambiguator {

    public QuestionDisambiguator() {
    }

    public List<String> disambiguateQuestion(String question, List<String> namedEntities) throws ParseException, IOException {
        AGDISTIS_Stream stream = new AGDISTIS_Stream();
        
        // surrounding every named entity in the question with <entity>...</entity>
        StringBuilder entityMarker = new StringBuilder(question);
        for(String ne : namedEntities){
            entityMarker.insert(entityMarker.indexOf(ne) + ne.length(), "</entity>");
            entityMarker.insert(entityMarker.indexOf(ne), "<entity>");
        }
        String entityMarkedQuestion = entityMarker.toString();
        
        // running the disambiguation on the question
        HashMap<String, String> results = stream.runDisambiguation(entityMarkedQuestion);
        
        // returning the disambiguation output
        List<String> URIs = new ArrayList();
        for (String namedEntity : results.keySet()) {
            URIs.add(results.get(namedEntity));
        }
        return URIs;
    }
}
