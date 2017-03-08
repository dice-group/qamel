package org.aksw.ldac.util;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;

import org.aksw.jena_sparql_api.cache.core.QueryExecutionFactoryCacheEx;
import org.aksw.jena_sparql_api.cache.extra.CacheCoreEx;
import org.aksw.jena_sparql_api.cache.extra.CacheCoreH2;
import org.aksw.jena_sparql_api.cache.extra.CacheExImpl;
import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.jena_sparql_api.delay.core.QueryExecutionFactoryDelay;
import org.aksw.jena_sparql_api.http.QueryExecutionFactoryHttp;
import org.aksw.jena_sparql_api.pagination.core.QueryExecutionFactoryPaginated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;

public class SparqlEndpoint {
	private static final String DBPEDIA_ENDPOINT = "DBPEDIA_ENDPOINT";
	private static final String DBPEDIA_NAMED_GRAPH = "DBPEDIA_NAMEDGRAPH";
	private static final String DBPEDIA_CACHE = "DBPEDIA_CACHE";
	private static final String HOTEL_ENDPOINT = "HOTEL_ENDPOINT";
	private static final String HOTEL_NAMEDGRAPH = "HOTEL_NAMEDGRAPH";
	private static final String HOTEL_CACHE = "HOTEL_CACHE";

	private QueryExecutionFactoryPaginated dbpediaEndpoint;
	private QueryExecutionFactoryPaginated hotelEndpoint;
	private static Logger log = LoggerFactory.getLogger(SparqlEndpoint.class);

	public SparqlEndpoint() {
		dbpediaEndpoint = createSPARLEndpoint(DBPEDIA_ENDPOINT, DBPEDIA_NAMED_GRAPH, DBPEDIA_CACHE);
		hotelEndpoint = createSPARLEndpoint(HOTEL_ENDPOINT, HOTEL_NAMEDGRAPH, HOTEL_CACHE);
	}

	private QueryExecutionFactoryPaginated createSPARLEndpoint(String sparqlEndpoint, String namedGraph, String cache) {
		try {
			// Create a query execution over DBpedia
			QueryExecutionFactory qef = new QueryExecutionFactoryHttp(getProperty(sparqlEndpoint), getProperty(namedGraph));

			/*
			 * Add delay in order to be nice to the remote server (delay in
			 * milli seconds)
			 */
			qef = new QueryExecutionFactoryDelay(qef, 7000);

			// Set up a cache, Cache entries are valid for 1 day
			long timeToLive = 24l * 60l * 60l * 1000l;

			/*
			 * This creates a 'cache' folder, with a database file named
			 * 'sparql.db' Technical note: the cacheBackend's purpose is to only
			 * deal with streams, whereas the frontend interfaces with higher
			 * level classes - i.e. ResultSet and Model
			 */

			CacheCoreEx cacheBackend;
			cacheBackend = CacheCoreH2.create(cache, timeToLive, true);
			CacheExImpl cacheFrontend = new CacheExImpl(cacheBackend);
			qef = new QueryExecutionFactoryCacheEx(qef, cacheFrontend);

			// Add pagination
			return new QueryExecutionFactoryPaginated(qef, 900);
		} catch (IOException e) {
			log.error("IOException: ", e);
		} catch (ClassNotFoundException e) {
			log.error("ClassNotFoundException: ", e);
		} catch (SQLException e) {
			log.error("SQLException: ", e);
		}
		return null;
	}

	private String getProperty(String property) throws IOException {
		Properties prop = new Properties();
		prop.load(getClass().getClassLoader().getResourceAsStream("config.properties"));
		return (prop.getProperty(property));
	}

	public static void main(String[] args) {
		SparqlEndpoint sp = new SparqlEndpoint();
		// String query =
		// "Select ?s { ?s a <http://dbpedia.org/ontology/City>. }";
		// log.debug(ResultSetFormatter.asText(sp.sparqlDBpedia(query)));
		String query = "Select * {<http://ontology.unister.de/ontology#Lake> ?p ?o}";
		log.debug(ResultSetFormatter.asText(sp.sparqlHotel(query)));
//	f
	}

	public ResultSet sparqlDBpedia(String query) {
		QueryExecution qe = dbpediaEndpoint.createQueryExecution(query);
		return qe.execSelect();
	}

	public ResultSet sparqlHotel(String query) {
		QueryExecution qe = hotelEndpoint.createQueryExecution(query);
		return qe.execSelect();
	}
}
