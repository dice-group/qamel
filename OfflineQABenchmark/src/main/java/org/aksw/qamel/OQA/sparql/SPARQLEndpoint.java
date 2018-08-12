package org.aksw.qamel.OQA.sparql;

import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.sparql.SPARQLRepository;

public class SPARQLEndpoint implements SPARQLInterface{
	@SuppressWarnings("unused")
	private static final String TAG = SPARQLEndpoint.class.getSimpleName();

	private RepositoryConnection connection;

	public SPARQLEndpoint(String sPARQLEndpoint) {
		SPARQLRepository sparqlrepository = new SPARQLRepository(sPARQLEndpoint);
		sparqlrepository.initialize();
		connection = sparqlrepository.getConnection();
	}

	@Override
	public TupleQueryResult query(String sparqlQuery) {
		TupleQuery tupleQuery = connection.prepareTupleQuery(sparqlQuery);
		TupleQueryResult evaluate = tupleQuery.evaluate();
		return evaluate;
	}


}
