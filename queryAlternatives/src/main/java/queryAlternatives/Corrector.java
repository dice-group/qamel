package queryAlternatives;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.Collection;

import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import org.deeplearning4j.models.word2vec.Word2Vec;

import org.slf4j.LoggerFactory;

@SuppressWarnings("unused")
public class Corrector {
		private static org.slf4j.Logger log = LoggerFactory.getLogger(Corrector.class);
		private static WordVectors vec;
		
	 	public Corrector(){
	 		log.info("...loading Wordvectors...");
	 		File gModel = new File(ClassLoader
	 				.getSystemResource("glove.6B.300d.txt")
	 				.getFile());		    
	 		vec = WordVectorSerializer.readWord2VecModel(gModel);
	 	};
	 	
	 	Collection<String> ask(String q, int n){
	 		log.info("...asking for n nearest words to " + q + ": ");
	 		return vec.wordsNearest(q, n);
	 	}
}
