package org.aksw.qamel.OQA.sparql;

import org.json.simple.JSONArray;

public interface SPARQLInterface {

	JSONArray query(String sparqlQuery) throws Exception;

}
