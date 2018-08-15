package org.aksw.qamel.OQA.sparql;

import org.aksw.qa.annotation.cache.PersistentCache;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.sparql.SPARQLRepository;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class SPARQLEndpoint implements SPARQLInterface {
	@SuppressWarnings("unused")
	private static final String TAG = SPARQLEndpoint.class.getSimpleName();

	private RepositoryConnection connection;

	private PersistentCache cache;

	private JSONParser parser;

	public SPARQLEndpoint(String sPARQLEndpoint) {
		SPARQLRepository sparqlrepository = new SPARQLRepository(sPARQLEndpoint);
		sparqlrepository.initialize();
		connection = sparqlrepository.getConnection();

		cache = new PersistentCache();
		parser = new JSONParser();
	}

	@Override
	public JSONArray query(String sparqlQuery) throws Exception {
		sparqlQuery = sparqlQuery.replace("\n", "").replace("\t", "");
		if (cache.containsKey(sparqlQuery)) {
			String tmp = cache.get(sparqlQuery);
			JSONArray obj = (JSONArray) parser.parse(tmp);

			return obj;
		} else {
			TupleQuery tupleQuery = connection.prepareTupleQuery(sparqlQuery);
			TupleQueryResult result = tupleQuery.evaluate();

			JSONArray list = new JSONArray();
			try {
				while (result.hasNext()) {
					BindingSet set = result.next();
					if (set.size() != 0) {
						JSONObject obj = new JSONObject();
						for (String b : result.getBindingNames()) {
							obj.put(b, set.getValue(b).stringValue());
						}
						list.add(obj);
					}
				}
				cache.put(sparqlQuery, list.toJSONString());

				cache.writeCache();
			} catch (Exception e) {

			}

			return list;
		}
	}

}
