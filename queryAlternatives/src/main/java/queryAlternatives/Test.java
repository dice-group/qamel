package queryAlternatives;

import java.io.File;
import java.util.Collection;

import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.Word2Vec;

import org.slf4j.LoggerFactory;

public class Test {
		private static org.slf4j.Logger log = LoggerFactory.getLogger(Test.class);
	
	 	public static void main(String[] args){
	 		File gModel = new File(ClassLoader.getSystemResource("GoogleNews-vectors-negative300.bin.gz").getFile());
		    Word2Vec vec = WordVectorSerializer.readWord2VecModel(gModel);
	        log.info("Closest Words:");
	        Collection<String> lst = vec.wordsNearest("town", 15);
	        System.out.println(lst);
	     }
}
