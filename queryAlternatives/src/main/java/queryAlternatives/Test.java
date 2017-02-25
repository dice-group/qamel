package queryAlternatives;

import java.util.List;

import edu.stanford.nlp.simple.Sentence;

public class Test {
	public static void main(String[] args){
		Corrector c = new Corrector();
		Index index = new Index();
		for(String res: index.search("Frage")){
			System.out.println(res);
		}
		Sentence sent = new Sentence("Berlin is the capital of germany.");
		List<String> posTags = sent.posTags();
		for(int i = 0; i < posTags.size(); i++){
			if(posTags.get(i).contains("NN")){
				String word = sent.word(i);
				System.out.println(c.ask(word, 10));
			}
		}
	}
}