/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SCNLP_Annotator;

import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLPClient;
import java.util.Properties;

/**
 *
 * @author Florian
 */
public class SCNLP_Stream {

    private StanfordCoreNLPClient pipeline;

    public SCNLP_Stream(String annotators) {
        // creates a StanfordCoreNLP object with the given annotators
        Properties props = new Properties();
        props.setProperty("annotators", annotators);
        
        pipeline = new StanfordCoreNLPClient(props, "139.18.2.39", 9000);
        //pipeline = new StanfordCoreNLPClient(props, "corenlp.run", 80);
        //pipeline = new StanfordCoreNLPClient(props, "localhost", 9000);
    }

    public Annotation annotateQuestion(String question) {
        // annotates the given question on the coreNLP server
        Annotation document = new Annotation(question);
        pipeline.annotate(document);
        return document;
    }
}
