package org.aksw.qamel.OQA.sparql;

import java.io.File;

import org.aksw.qa.annotation.cache.PersistentCache;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.sail.nativerdf.NativeStore;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class TripleStore implements SPARQLInterface {
	@SuppressWarnings("unused")
	private static final String TAG = TripleStore.class.getSimpleName();

	private Repository sDatabase;
	private RepositoryConnection connection;

	private PersistentCache cache;

	private JSONParser parser;

	public TripleStore(String database) {
		File dbDir = new File(database);
		sDatabase = new SailRepository(new NativeStore(dbDir));
		sDatabase.initialize();
		sDatabase.isInitialized();

		connection = sDatabase.getConnection();

		cache = new PersistentCache();
		parser = new JSONParser();
	}

	@Override
	public JSONArray query(String sparqlQuery) throws ParseException {
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
