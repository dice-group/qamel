
package de.qa.qa.triplestore;

import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.sail.nativerdf.NativeStore;

import java.io.File;

public class TripleStore {
    private static final String TAG = TripleStore.class.getSimpleName();

    private Repository sDatabase;
    private static RepositoryConnection connection;

    public TripleStore(String database) {
        File dbDir = new File(database);
        sDatabase = new SailRepository(new NativeStore(dbDir));
        sDatabase.initialize();
        sDatabase.isInitialized();

        connection = sDatabase.getConnection();
    }

    public static TupleQueryResult query(String database, String sparqlQuery) {
        TupleQuery tupleQuery = connection.prepareTupleQuery(sparqlQuery);
        TupleQueryResult evaluate = tupleQuery.evaluate();
        return evaluate;
    }

    public void printTupleResult(TupleQueryResult result) {
        while (result.hasNext()) {
            BindingSet set = result.next();
            for (String s : set.getBindingNames()) {
                System.out.println(s + ": " + set.getValue(s).stringValue());
            }
        }
    }

}