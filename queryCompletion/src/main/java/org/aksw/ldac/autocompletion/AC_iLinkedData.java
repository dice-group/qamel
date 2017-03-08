package org.aksw.ldac.autocompletion;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import org.aksw.ldac.eval.Evaluation;
import org.aksw.ldac.nlp.Segmenter;
import org.aksw.ldac.util.SparqlEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AC_iLinkedData implements AutoCompletion {

	private static Logger log = LoggerFactory.getLogger(AC_iLinkedData.class);
	private SparqlEndpoint sparql;
	private Segmenter seg;

	public static void main(String[] args) throws UnsupportedEncodingException {

		ArrayList<String> testQueries = new ArrayList<String>();
		testQueries.add("Hotel auf Mallorca");
		testQueries.add("Hotel Hamburg");
		testQueries.add("Hotel Berlin mit WLAN");
		testQueries.add("Hotel auf Mallorca");
		testQueries.add("Hotel auf Mallorca");
		testQueries.add("Hotel in Dominikanische Republik");
		testQueries.add("Malediven buchen Hotel");
		testQueries.add("T�rkei Bewertung Urlaub");
		testQueries.add("Louvre Bewertung buchen");
		testQueries.add("Last Minute Reisen lastminute Flug Flughafen");
		testQueries.add("T�rkei istanbul");

		AutoCompletion ac = new AC_iLinkedData();
		for (String query : testQueries) {
			// TODO hack for wrongly encoded dumbs
			if (!query.contains("�")) {
				log.debug(URLDecoder.decode(query, "UTF8"));
			}
		}
		Evaluation ev = new Evaluation();
		ev.setTestQueries(testQueries);
		ev.setAutoCompletionAlgortihm(ac);
		log.debug("" + ev.getAreaUnderCurveBetweenLengthOfQueryAndPercentCorrectQueries());
	}

	public AC_iLinkedData() {
		this.sparql = new SparqlEndpoint();
		this.seg = new Segmenter(sparql);

	}

	/**
	 * definition: valid segments are segments that have a corresponding surface
	 * form PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> Select * { ?s
	 * rdfs:label "SEGMENT"@de }
	 */
	public String getFullQuery(String substring) {
		List<List<String>> segments = seg.getSegmentsRecursively(substring);

		if (!segments.isEmpty()) {
			List<String> topSuggestions = new ArrayList<String>();
			for (List<String> segment : segments) {
				// classify substring whether it is a concept or instance
				if (isConcept(substring)) {
					topSuggestions = getConceptMatches(segment);
				} else {
					topSuggestions = getInstanceMatches(segment);
				}
			}
			// rank after popularity
			topSuggestions = rank(topSuggestions);

			return topSuggestions.get(0);
		}
		return substring;
	}

	private List<String> rank(List<String> topSuggestions) {
		// TODO Auto-generated method stub
		return topSuggestions;
	}

	private List<String> getInstanceMatches(List<String> segment) {
		// TODO Auto-generated method stub
		return null;
	}

	private List<String> getConceptMatches(List<String> segment) {
		// TODO Auto-generated method stub
		return null;
	}

	private boolean isConcept(String substring) {
		// TODO Auto-generated method stub
		return false;
	}

	public void setTrainingQueries(InputStream training) {
	}

	public String toString() {
		return "iLinkedData";
	}

}
