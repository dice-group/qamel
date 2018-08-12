package org.aksw.qamel.OQA.sparql;

import org.eclipse.rdf4j.query.TupleQueryResult;

public interface SPARQLInterface {

	TupleQueryResult query(String sparqlQuery);

}
