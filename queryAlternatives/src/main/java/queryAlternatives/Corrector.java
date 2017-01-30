package queryAlternatives;

import java.io.File;
import java.util.Collection;

import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.Word2Vec;

import org.slf4j.LoggerFactory;

public class Corrector {
		private static org.slf4j.Logger log = LoggerFactory.getLogger(Corrector.class);
		private static Word2Vec vec;
		
	 	public Corrector(){
	 		File gModel = new File(ClassLoader.getSystemResource("GoogleNews-vectors-negative300.bin.gz").getFile());
		    vec = WordVectorSerializer.readWord2VecModel(gModel);
	        log.info("Closest Words:");
	        Collection<String> lst = vec.wordsNearest("city", 15);
	        System.out.println(lst);
	     }
}
