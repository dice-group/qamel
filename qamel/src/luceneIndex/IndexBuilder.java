package luceneIndex;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.stream.Stream;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.MMapDirectory;
import org.apache.lucene.util.Version;



import org.slf4j.LoggerFactory;

public class IndexBuilder {
	private static final Version LUCENE_VERSION = Version.LUCENE_6_3_0;
	private org.slf4j.Logger log = LoggerFactory.getLogger(IndexBuilder.class);
	private int numberOfDocsRetrievedFromIndex = 100;

	public String FIELD_NAME_URI = "uri";

	private Directory directory;
	private IndexSearcher isearcher;
	private DirectoryReader ireader;
	private IndexWriter iwriter;
	private Analyzer analyzer;

	
	void addURItoIndex(){
		
	}
	
	void getURIsFromKB(){
		//Model model = ModelFactory.createDefaultModel();
	}
	
	
	void buildIndex(){
		try {
			File indexDir = new File("resources/KnowledgeBase");
			Path indexPath = indexDir.toPath();
			analyzer = new StandardAnalyzer();
	
			if (!indexDir.exists()) {
	
				indexDir.mkdir();
				IndexWriterConfig config = new IndexWriterConfig(analyzer);
				directory = new MMapDirectory(indexPath);
				iwriter = new IndexWriter(directory, config);

/*
 * 			Create the Index
 */
			
	
			} else {
				directory = new MMapDirectory(indexPath);
			}
			ireader = DirectoryReader.open(directory);
			isearcher = new IndexSearcher(ireader);
		} catch (IOException e) {
			log.error(e.getLocalizedMessage(), e);
		}
	}

}
