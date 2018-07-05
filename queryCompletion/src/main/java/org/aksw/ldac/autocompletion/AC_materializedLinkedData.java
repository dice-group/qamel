//package org.aksw.ldac.autocompletion;
//
//import java.io.IOException;
//import java.io.InputStream;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import com.unister.semweb.wordtree.core.SparseByteArrayTreeFactory;
//import com.unister.semweb.wordtree.core.WordTree;
//
//public class AC_materializedLinkedData implements AutoCompletion {
//
//	private Logger log = LoggerFactory.getLogger(this.getClass());
//	private WordTree tree;
//
//	public AC_materializedLinkedData(String materializedGrammar) {
//		SparseByteArrayTreeFactory factory = new SparseByteArrayTreeFactory();
//		try {
//			WordTree tree = factory.load(materializedGrammar);
//			tree.fillWords();
//			this.tree = tree;
//		} catch (IOException e) {
//			log.error(e.getLocalizedMessage());
//		}
//	}
//
//	public String getFullQuery(String substring) {
//		int positionInPrefixTree = tree.positionAsPart(substring);
//		// if substring not in prefix tree
//		if (positionInPrefixTree == -1)
//			return substring;
//		log.debug(substring);
//		try {
//			return tree.rebuildWordFromNodeIndex(positionInPrefixTree);
//		} catch (ArrayIndexOutOfBoundsException e) {
//			log.error("ArrayIndexOutOfBoundsException ->substring: " + substring);
//		}
//		return substring;
//	}
//
//	public void setTrainingQueries(InputStream training) {
//	}
//
//	public String toString() {
//		return "materialized LD";
//	}
//
//}
