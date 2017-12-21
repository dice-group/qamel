/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SCNLP_Annotator;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.EntityMentionsAnnotator;
import edu.stanford.nlp.util.CoreMap;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Florian
 */
public class QuestionAnnotator {

    public QuestionAnnotator() {
    }

    public List<String> findNamedEntities(String questionString) {
        List<String> namedEntities = new ArrayList();

        // annoating the question
        SCNLP_Stream stream = new SCNLP_Stream("tokenize, ssplit, pos, lemma, ner");
        Annotation question = stream.annotateQuestion(questionString);
        
        /* This part is (temporarily) not needed
        
        //Interpreting the annotations:
        // a CoreMap is essentially a Map that uses class objects as keys and has values with custom types
        
        List<CoreMap> sentences = question.get(CoreAnnotations.SentencesAnnotation.class);
        
        for (CoreMap sentence : sentences) {
            // traversing the words in the current sentence
            // a CoreLabel is a CoreMap with additional token-specific methods
            for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                // this is the text of the token
                String word = token.get(CoreAnnotations.TextAnnotation.class);
                // this is the NER (Named Entity Recognition) label of the token
                String ne = token.get(CoreAnnotations.NamedEntityTagAnnotation.class);
                // this is the lemma (basic form) label of the token
                String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);

                // adding the NE to the list
                if (!ne.equals("O")) {
                    namedEntities.add(word);
                }
            }
        }
        */
        
        // annotating a second time, so that entities consisting of several words are handled as one entity
        EntityMentionsAnnotator mentionsAnnotator = new EntityMentionsAnnotator();
        mentionsAnnotator.annotate(question);
        
        // adding the named entities to the list and returning them
        List<CoreMap> mentions = question.get(CoreAnnotations.MentionsAnnotation.class);
        for (CoreMap mention: mentions) {
            namedEntities.add(mention.get(CoreAnnotations.TextAnnotation.class));
        }
        
        return namedEntities;
    }

}
