package org.aksw.ldac.input;

import static org.junit.Assert.assertTrue;

import org.aksw.ldac.input.sparql2nl.SPARQL2NL;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SPARQLQueryLogReaderTest {
	Logger log = LoggerFactory.getLogger(SPARQLQueryLogReaderTest.class);

	@Test
	public void sparqlToNLTest() {
		SPARQL2NL sqlr = new SPARQL2NL();
		String query = "SELECT * WHERE {<http://dbpedia.org/resource/Paris> <http://dbpedia.org/ontology/populationTotal> ?a.}";
		String nl = "Paris population total";
		String generatedNL = sqlr.sparqlToNaturalLanguage(query);
		log.info(generatedNL);
		assertTrue(nl.equals(generatedNL));
	}

	@Test
	public void logToSparqlTest()  {
		SPARQL2NL sqlr = new SPARQL2NL();
		String logLine = "f8fe165068e953206864c824265ad94a - - [26/Jul/2012 04:00:00 +0200] \"/sparql/?query=SELECT+%3Fabstract+WHERE+{+%3Chttp%3A%2F%2Fdbpedia.org%2Fresource%2FCarnegie_Council_for_Ethics_in_International_Affairs%3E+%3Chttp%3A%2F%2Fdbpedia.org%2Fontology%2Fabstract%3E+%3Fabstract.+FILTER+langMatches(lang(%3Fabstract)%2C+%27en%27)+}&format=json\" 200 1024 \"-\" \"-\"";
		String expectedResult = "SELECT ?abstract WHERE { <http://dbpedia.org/resource/Carnegie_Council_for_Ethics_in_International_Affairs> <http://dbpedia.org/ontology/abstract> ?abstract. FILTER langMatches(lang(?abstract), 'en') }";
		String query = sqlr.log2Sparql(logLine);
		log.info(query);
		assertTrue(query.equals(expectedResult));
	}
}
