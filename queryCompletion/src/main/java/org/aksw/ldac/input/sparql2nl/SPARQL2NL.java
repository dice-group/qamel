package org.aksw.ldac.input.sparql2nl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QueryParseException;
import com.hp.hpl.jena.sparql.syntax.ElementWalker;

public class SPARQL2NL {
	static Logger log = LoggerFactory.getLogger(SPARQL2NL.class);

	public static void main(String args[]) throws IOException {

		SPARQL2NL sparql2nl = new SPARQL2NL();
		BufferedReader br = new BufferedReader(new FileReader("/data/r.usbeck/DbpediaSPARQLLogs/access.log-20120727"));
		BufferedWriter bw = new BufferedWriter(new FileWriter("verbalizedDBpedia38logs.txt"));
		int numberOfAtLeastBoundVariables = 3;

		int i = 0;
		while (br.ready()) {
			String log = br.readLine();
			String query = sparql2nl.log2Sparql(log);
			if (query != null) {
				String nl = sparql2nl.sparqlToNaturalLanguage(query, numberOfAtLeastBoundVariables);
				if (nl != null) {
					bw.write(nl);
					bw.newLine();
//					log.debug(nl);
					i++;
					if (i % 100 == 0)
						bw.flush();
				}
			}
		}
		br.close();
		bw.close();
	}

	public String sparqlToNaturalLanguage(String query, int numberOfBoundVariables) {
		// analyze query for bound variables
		if (numberOfBoundVariables <= numberOfBoundVariables(query))
			return sparqlToNaturalLanguage(query);
		else
			return null;
	}

	private int numberOfBoundVariables(String query) {
		try {
			Query q = QueryFactory.create(query);
			ElementBindingCounterVisitor visitor = new ElementBindingCounterVisitor();
			ElementWalker.walk(q.getQueryPattern(), visitor);
			return visitor.getNumberOfBoundVariables();
		} catch (QueryParseException e) {
			log.warn(e.getLocalizedMessage());
		}
		return 0;
	}

	public String sparqlToNaturalLanguage(String query) {
		// analyze query for bound variables
		try {
			Query q = QueryFactory.create(query);
			handleProjection(q, q.isSelectType() && q.isQueryResultStar());
			log.debug("getDatasetDescription" + q.getDatasetDescription());
			log.debug("getQueryPattern" + q.getQueryPattern());
			log.debug("getQueryPattern" + q.getQueryPattern());
			// verbalize them
			ElementVerbalizationVisitor visitor = new ElementVerbalizationVisitor();
			visitor.setNaturalLanguangeQuery(new String());
			ElementWalker.walk(q.getQueryPattern(), visitor);
			return visitor.getNaturalLanguangeQuery();
		} catch (QueryParseException e) {
			log.warn(e.getLocalizedMessage());
		} catch (NoSuchElementException e) {
			log.warn(e.getLocalizedMessage());
		}
		return null;
	}

	private void handleProjection(Query q, boolean isStarProjection) {
		// TODO Auto-generated method stub
		// of the form SELECT *?

	}

	public String log2Sparql(String logLine) {
		String query = null;
		try {
			String regex = "query=[a-zA-Z0-9\\+\\%\\{\\}\\(\\)@\\._]+";
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(logLine);
			matcher.find();
			query = URLDecoder.decode(matcher.group(0), "utf-8");
			query = query.substring("query=".length());
		} catch (UnsupportedEncodingException e) {
			log.error(e.getLocalizedMessage());
		} catch (IllegalStateException e) {
			log.warn(e.getLocalizedMessage());
		}

		return query;
	}
}
