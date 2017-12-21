/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SPARQL_Executer;

/**
 *
 * @author Florian
 */
import java.io.IOException;
import java.net.URLEncoder;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SparqlQueryExecuter {

    public SparqlQueryExecuter() {
    }

    public String execute(String query) throws IOException {
        // Creating the URL to execute the query against the DBpedia SPARQL endpoint
        String prefix = "http://dbpedia.org/sparql?default-graph-uri=http%3A%2F%2Fdbpedia.org&query=";
        String postfix = "&format=application%2Fsparql-results%2Bjson&timeout=30000";
        String encodedQuery = URLEncoder.encode(query, "UTF-8");

        String url = prefix + encodedQuery + postfix;
        System.out.println(url);
        
        // Connecting to the DBpedia SPARQL endpoint
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        
        // Returning the response json
        Response response = client.newCall(request).execute();
        return response.body().string();

    }
}
