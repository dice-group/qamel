package org.aksw.ldac.input.sparql2nl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.ResultSet;

public class DBPedia {
	private Logger log = LoggerFactory.getLogger(DBPedia.class);
	private String sparqlEndpoint;

	public DBPedia() {
		sparqlEndpoint = "http://dbpedia.org/sparql";
	}

	/**
	 * returns a matrix of query results where each row represents a result and
	 * each column a variable projection of the query
	 * 
	 * @param query
	 * @return matrix of results, null if an exception occured
	 */
	public ResultSet askDbpedia(String query) {
		QueryExecution x = QueryExecutionFactory.sparqlService(sparqlEndpoint, query);
		ResultSet results = x.execSelect();
		log.debug(results.toString());
		return results;
	}

}