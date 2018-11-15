package de.qa.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.MalformedQueryException;
import org.eclipse.rdf4j.query.TupleQueryResult;

import java.io.File;

import de.qa.R;
import de.qa.qa.OfflineQuestionAnswerer;
import de.qa.qa.triplestore.TripleStore;

/**
 * Test class - to be removed in release version.
 */
public class SparqlActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sparql);
    }


    public void executeSparQL(View view) {
        EditText editText = findViewById(R.id.sparqlEditText);
        String query = editText.getText().toString();
        try {
            StringBuilder out = new StringBuilder();
/*
            TupleQueryResult result = OfflineQuestionAnswerer.tripleStore.query(query);
            while (result.hasNext()) {
                BindingSet set = result.next();
                for (String s : set.getBindingNames()) {
                    out.append(s).append(": ").append(set.getValue(s).stringValue()).append("\n");
                }
            }*/
            ((TextView) findViewById(R.id.resultText)).setText(out.toString());
        } catch (MalformedQueryException e) {
            Toast.makeText(this, "Invalid query", Toast.LENGTH_SHORT).show();
        }
    }
}
