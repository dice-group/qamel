package org.aksw.qamel.OQA.sparql;

import java.io.File;

import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.sail.nativerdf.NativeStore;

public class TripleStore implements SPARQLInterface{
	@SuppressWarnings("unused")
	private static final String TAG = TripleStore.class.getSimpleName();

	private Repository sDatabase;
	private RepositoryConnection connection;

	public TripleStore(String database) {
		 File dbDir = new File(database);
		 sDatabase = new SailRepository(new NativeStore(dbDir));
		 sDatabase.initialize();
		 sDatabase.isInitialized();
		
		connection = sDatabase.getConnection();
	}

	@Override
	public TupleQueryResult query(String sparqlQuery) {
		TupleQuery tupleQuery = connection.prepareTupleQuery(sparqlQuery);
		TupleQueryResult evaluate = tupleQuery.evaluate();
		return evaluate;
	}


}
