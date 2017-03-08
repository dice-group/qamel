package org.aksw.ldac.autocompletion;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.unister.semweb.wordtree.core.SparseByteArrayTreeFactory;
import com.unister.semweb.wordtree.core.WordTree;

public class AC_PrefixTrie implements AutoCompletion {
	public AC_PrefixTrie() {
		this.tree = new WordTree();
	}

	Logger log = LoggerFactory.getLogger(AC_PrefixTrie.class);
	private WordTree tree;

	public String getFullQuery(String substring) {

		int positionInPrefixTree = tree.positionAsPart(substring);
		// if substring not in prefix tree
		if (positionInPrefixTree == -1)
			return substring;
		log.debug(substring);
		try {
			return tree.rebuildWordFromNodeIndex(positionInPrefixTree);
		} catch (ArrayIndexOutOfBoundsException e) {
			log.error("ArrayIndexOutOfBoundsException ->substring: " + substring);
		}
		return substring;
	}

	public void setTrainingQueries(InputStream queryLogFile) {
		SparseByteArrayTreeFactory factory = new SparseByteArrayTreeFactory();
		try {
			WordTree tree = factory.load(queryLogFile);
			tree.fillWords();
			for (String word : tree.insertedWords) {
				this.tree.insert(word);
			}
		} catch (FileNotFoundException e) {
			log.error(e.getLocalizedMessage());
		} catch (IOException e) {
			log.error(e.getLocalizedMessage());
		}
	}	
	
	public String toString(){
		return "Prefix Tree";
	}

}
