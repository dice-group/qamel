
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

    private static Repository sDatabase;

    private static void openDatabase(String database) {
        File dbDir = new File(database);
        System.out.println("Database directory"+dbDir);
        sDatabase = new SailRepository(new NativeStore(dbDir));
        System.out.println("Initializing database...");
        sDatabase.initialize();
        System.out.println("Done.");
    }

    public static TupleQueryResult query(String database, String sparqlQuery) {
        if (sDatabase == null) openDatabase(database);
        RepositoryConnection connection = sDatabase.getConnection();
        TupleQuery tupleQuery = connection.prepareTupleQuery(sparqlQuery);
        return tupleQuery.evaluate();
    }

    public static void printTupleResult(TupleQueryResult result) {
        while (result.hasNext()) {
            BindingSet set = result.next();
            System.out.println("---");
            for (String s : set.getBindingNames()) {
                System.out.println(s + ": " + set.getValue(s).stringValue());
            }
        }
    }
}


