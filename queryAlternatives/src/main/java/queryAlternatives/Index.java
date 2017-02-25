package queryAlternatives;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
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
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.QueryBuilder;
import org.slf4j.LoggerFactory;

public class Index {
	//TODO: Filter out Disambiguations
	
	private org.slf4j.Logger log = LoggerFactory.getLogger(Index.class);
	private int numberOfDocsRetrievedFromIndex = 10;

	public String FIELD_NAME_TEXT = "text";   //search key
	public String FIELD_NAME_SENT = "sentence"; //correct label

	private Directory directory;
	private IndexSearcher isearcher;
	private DirectoryReader ireader;
	private IndexWriter iwriter;
	private Analyzer analyzer;
	private File indexDir;

	public Index() {
		indexDir = new File("index");
		if(!indexDir.exists()){
			buildIndex();
		}
		else{
			Path indexPath = Paths.get(indexDir.getAbsolutePath());
			try {
				directory = FSDirectory.open(indexPath);
				ireader = DirectoryReader.open(directory);
				isearcher = new IndexSearcher(ireader);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void addDocumentToIndex(String sentence, String text) throws IOException {
		Document doc = new Document();
		doc.add(new StringField(FIELD_NAME_SENT, sentence, Store.YES));
		doc.add(new TextField(FIELD_NAME_TEXT, text, Store.YES));
		iwriter.addDocument(doc);
	}

	private void buildIndex() {
		try {
			Path indexPath = Paths.get(indexDir.getAbsolutePath());
			analyzer = new StandardAnalyzer();
			System.out.println("creating Index..");
			indexDir.mkdir();
			IndexWriterConfig config = new IndexWriterConfig(analyzer);
			directory = FSDirectory.open(indexPath);
			iwriter = new IndexWriter(directory, config);
			InputStream resourceToLoad = getClass().getResourceAsStream("/test.log");
			BufferedReader br = new BufferedReader(new InputStreamReader(resourceToLoad));
			String line;
			while((line = br.readLine()) != null){
				addDocumentToIndex(line, line);
			}
			iwriter.close();
			br.close();
		} catch (IOException e) {
			log.error(e.getLocalizedMessage(), e);
		}
	}

	public HashSet<String> search(String word) {
		ArrayList<String> uris = new ArrayList<String>();
		try {
			log.debug("\t start asking index...");
			analyzer = new StandardAnalyzer();
			QueryBuilder builder = new QueryBuilder(analyzer);
			Query q = builder.createPhraseQuery(FIELD_NAME_TEXT, word);
			TopScoreDocCollector collector = TopScoreDocCollector.create(numberOfDocsRetrievedFromIndex);
			isearcher.search(q, collector);
			ScoreDoc[] hits = collector.topDocs().scoreDocs;
			for (int i = 0; i < hits.length; i++) {
				Document hitDoc = isearcher.doc(hits[i].doc);
				uris.add(hitDoc.get(FIELD_NAME_SENT));
			}
			log.debug("\t finished asking index...");
		} catch (Exception e) {
			log.error(e.getLocalizedMessage() + " -> " + word, e);
		}
		HashSet<String> setUris = new HashSet<String>();
		for (int i = 0; i < uris.size(); i++) {
			setUris.add(uris.get(i));
		}
		return setUris;
	}

	
}
