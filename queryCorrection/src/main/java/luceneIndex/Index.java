package luceneIndex;

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
import org.openrdf.model.Statement;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.RDFParser;
import org.openrdf.rio.helpers.RDFHandlerBase;
import org.openrdf.rio.turtle.TurtleParser;
import org.slf4j.LoggerFactory;

public class Index {
	//TODO: Filter out Disambiguations
	
	private org.slf4j.Logger log = LoggerFactory.getLogger(Index.class);
	private int numberOfDocsRetrievedFromIndex = 10;

	public String FIELD_NAME_URI = "uri";   //search key
	public String FIELD_NAME_cURI = "curi"; //correct label

	private Directory directory;
	private IndexSearcher isearcher;
	private DirectoryReader ireader;
	private IndexWriter iwriter;
	private Analyzer analyzer;
	private static HashSet<String> predicates;
	private static HashMap<String, String> redirects;
	private static HashMap<String, String> labels;
	private File indexDir;

	public Index() {
		predicates = new HashSet<String>();
		predicates.add("http://www.w3.org/2000/01/rdf-schema#label");
		indexDir = new File("index");
		if(!indexDir.exists()){
			System.out.println("getting Labels..");
			labels = getLabels();
			System.out.println("getting Redirects..");
			redirects = getRedirects();
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

	private void addDocumentToIndex(String correctName, String name) throws IOException {
		Document doc = new Document();
		doc.add(new StringField(FIELD_NAME_URI, name, Store.YES));
		doc.add(new StringField(FIELD_NAME_cURI, correctName, Store.YES));
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

				/*
				 * Create the Index
				 */
			RDFParser parser = new TurtleParser();
			parser.setRDFHandler(new RDFHandlerBase(){
				@Override
				public void handleStatement(Statement st) {
					String subject = st.getSubject().stringValue();
					String predicate = st.getPredicate().toString();
					String object = st.getObject().stringValue();
					String correctSubject = subject;
					String correctObject = object;
					if (redirects.get(subject) != null) {
						correctSubject = redirects.get(subject);
					}
					if (labels.get(correctSubject) != null) {
						correctObject = labels.get(correctSubject);
					}
					if (predicates.contains(predicate)) {
						try {
							addDocumentToIndex(correctObject, object);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			});
			parser.setStopAtFirstError(false);
			InputStream resourceToLoad = getClass().getResourceAsStream("/labels_en.ttl");
			parser.parse(new InputStreamReader(resourceToLoad), "http://dbpedia.org/resource/");
			iwriter.close();
		} catch (RDFHandlerException | RDFParseException | IOException e) {
			log.error(e.getLocalizedMessage(), e);
		}
	}

	public HashSet<String> fuzzySearch(String uri) {
		ArrayList<String> uris = new ArrayList<String>();
		try {
			log.debug("\t start asking index...");
			Query q = new FuzzyQuery(new Term(FIELD_NAME_URI, uri), 2, 0, 50, true);
			TopScoreDocCollector collector = TopScoreDocCollector.create(numberOfDocsRetrievedFromIndex);
			isearcher.search(q, collector);
			ScoreDoc[] hits = collector.topDocs().scoreDocs;
			for (int i = 0; i < hits.length; i++) {
				Document hitDoc = isearcher.doc(hits[i].doc);
				uris.add(hitDoc.get(FIELD_NAME_cURI));
			}
			log.debug("\t finished asking index...");
		} catch (Exception e) {
			log.error(e.getLocalizedMessage() + " -> " + uri, e);
		}
		HashSet<String> setUris = new HashSet<String>();
		for (int i = 0; i < uris.size(); i++) {
			setUris.add(uris.get(i));
		}
		return setUris;
	}

	private HashMap<String, String> getRedirects(){
		final HashMap<String, String> redirects = new HashMap<String, String>();
		RDFParser redirectparser = new TurtleParser();
		redirectparser.setRDFHandler(new RDFHandlerBase(){
			@Override
			public void handleStatement(Statement st) {
				String subject = st.getSubject().stringValue();
				String object = st.getObject().stringValue();
				redirects.put(subject, object);
				}
		});
		redirectparser.setStopAtFirstError(false);
		InputStream resourceToLoad = getClass().getResourceAsStream("/redirects_en.ttl");
		try {
			redirectparser.parse(new InputStreamReader(resourceToLoad), "http://dbpedia.org/resource/");
		} catch (RDFHandlerException | RDFParseException | IOException e) {
			log.error(e.getLocalizedMessage(), e);
		}
		return redirects;
	}
	
	private HashMap<String, String> getLabels(){
		final HashMap<String, String> labels = new HashMap<String, String>();
		RDFParser labelparser = new TurtleParser();
		labelparser.setRDFHandler(new RDFHandlerBase(){
			@Override
			public void handleStatement(Statement st) {
				String subject = st.getSubject().stringValue();
				String object = st.getObject().stringValue();
				labels.put(subject, object);
			}
		});
		labelparser.setStopAtFirstError(false);
		InputStream resourceToLoad = getClass().getResourceAsStream("/labels_en.ttl");
		try {
			labelparser.parse(new InputStreamReader(resourceToLoad), "http://dbpedia.org/resource/");
		} catch (RDFHandlerException | RDFParseException | IOException e) {
			log.error(e.getLocalizedMessage(), e);
		}
		return labels;
	}
}
