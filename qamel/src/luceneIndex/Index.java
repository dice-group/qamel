package luceneIndex;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.NodeIterator;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.util.FileManager;
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
import org.apache.lucene.store.FSDirectory;
//import org.apache.lucene.util.Version;



import org.slf4j.LoggerFactory;

public class Index {
	//private static final Version LUCENE_VERSION = Version.LUCENE_6_3_0;
	private org.slf4j.Logger log = LoggerFactory.getLogger(Index.class);
	private int numberOfDocsRetrievedFromIndex = 10;

	public String FIELD_NAME_URI = "uri";

	private Directory directory;
	private IndexSearcher isearcher;
	private DirectoryReader ireader;
	private IndexWriter iwriter;
	private Analyzer analyzer;

	
	private void addDocumentToIndex(String name) throws IOException {
		Document doc = new Document();
		doc.add(new StringField(FIELD_NAME_URI, name, Store.YES));
		iwriter.addDocument(doc);
		System.out.println(name);
	}

	
	
	void buildIndex(){
		try {
			File indexDir = new File("resources/index");
			Path indexPath = Paths.get(indexDir.getAbsolutePath());
			analyzer = new StandardAnalyzer();
			
			if (!indexDir.exists()) {
				indexDir.mkdir();
				IndexWriterConfig config = new IndexWriterConfig(analyzer);
				directory = FSDirectory.open(indexPath);
				iwriter = new IndexWriter(directory, config);

/*
 * 			Create the Index
 */
				Model model = FileManager.get().loadModel( "./resources/test.nt" );
				NodeIterator objectsIterator = model.listObjects();
				ResIterator subjectsIterator = model.listSubjects();
				while(objectsIterator.hasNext()){
					try{
					String nextObject =  objectsIterator.next().asResource().getLocalName();
					addDocumentToIndex(nextObject);
					}catch(Exception e){
					}
				}
				while(subjectsIterator.hasNext()){
					try{
					String nextSubject = subjectsIterator.next().asResource().getLocalName();
					addDocumentToIndex(nextSubject);
					}catch(Exception e){
					}
				}
				iwriter.close();
			} else{
				directory = FSDirectory.open(indexPath);
			}
			ireader = DirectoryReader.open(directory);
			isearcher = new IndexSearcher(ireader);
		} catch (IOException e) {
			log.error(e.getLocalizedMessage(), e);
		}
	}
	
	public HashSet<String> search(String uri) {
		ArrayList<String> uris = new ArrayList<String>();
		try {
			System.out.println("\t start asking index...");

			Query q = new FuzzyQuery(new Term(FIELD_NAME_URI, uri), 2, 0, 50, true);
			TopScoreDocCollector collector = TopScoreDocCollector.create(numberOfDocsRetrievedFromIndex);
			isearcher.search(q, collector);
			ScoreDoc[] hits = collector.topDocs().scoreDocs;
			for (int i = 0; i < hits.length; i++) {
				Document hitDoc = isearcher.doc(hits[i].doc);
				uris.add(hitDoc.get(FIELD_NAME_URI));
			}
			System.out.println("\t finished asking index...");
		} catch (Exception e) {
			log.error(e.getLocalizedMessage() + " -> " + uri, e);
		}
		HashSet<String> setUris = new HashSet<String>();
		for(int i = 0; i < uris.size(); i++){
			setUris.add(uris.get(i));
		}
		return setUris;
	}
}
