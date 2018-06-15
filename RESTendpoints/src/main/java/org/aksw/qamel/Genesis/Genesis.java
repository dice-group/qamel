package org.aksw.qamel.Genesis;

import com.cloudant.client.api.ClientBuilder;
import com.cloudant.client.api.CloudantClient;
import com.cloudant.client.api.Database;
import com.cloudant.client.api.views.Key;
import org.apache.jena.query.*;
import org.apache.jena.sparql.engine.http.QueryEngineHTTP;

import java.net.URL;
import java.util.*;

/**
 * Created by rgathrey on 1/18/18.
 */
public class Genesis {
	CloudantClient cloudantClient;
	Database explorerDB;

	/*String couchDBUrl = "<COUCH DB URL>";
    String couchDBUsername = "<COUCH DB USERNAME>";
    String couchDBPassword = "<COUCH DB PASSWORD>";
    String couchDBName = "<COUCH DB DATABASE>";*/

	private static final int MAX_FIELD_SIZE = 5;        // Top N Properties that you want
	private static final int API_TIMEOUT = 5000;
	private static final String ENDPOINT = "https://dbpedia.org/sparql";

	String uri = "http://dbpedia.org/resource/Barack_Obama"; //  URI you want to test

	public Genesis() {

		process();

	}

	private static final String PREFIXES = new String(
			"PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n" +
					"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"+
					"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
					"PREFIX dbo: <http://dbpedia.org/ontology/>\n" +
					"PREFIX dbp: <http://dbpedia.org/property/>\n" +
					"PREFIX dbr: <http://dbpedia.org/resource/>\n" +
					"PREFIX dct: <http://purl.org/dc/terms/>\n"
			);


	public static String buildQuery(String query) {
		return PREFIXES + query;
	}

	public static QueryExecution executeQuery(String queryString) {

		Query query = QueryFactory.create(queryString);
		//System.out.println("Query execution started: "+query);
		QueryEngineHTTP queryEngine = (QueryEngineHTTP) QueryExecutionFactory.sparqlService(ENDPOINT, query);
		//System.out.println("Query Engine http: "+queryEngine);
		queryEngine.addParam("timeout", String.valueOf(API_TIMEOUT));
		return queryEngine;
	}

	public void process() {
		try {
			String query = buildQuery(
					"SELECT (GROUP_CONCAT(distinct ?type;separator=' ') as ?types) (GROUP_CONCAT(distinct ?property;separator=' ') as ?properties) WHERE {\n" +
							"<" + uri + "> rdf:type ?type . FILTER(STRSTARTS(STR(?type), 'http://dbpedia.org/ontology')) . \n" +
							"<" + uri + "> ?property ?value . FILTER(STRSTARTS(STR(?property), 'http://dbpedia.org/ontology')) . \n" +
					"}");
			QueryExecution queryExecution = executeQuery(query); // Get all Ontology Classes and Properties for given entity
			try {
				Iterator<QuerySolution> results = queryExecution.execSelect();
				while(results.hasNext()) {
					QuerySolution solution = results.next();
					if(solution.get("types") != null && solution.get("properties") != null) {
						List<String> types = Arrays.asList(solution.get("types").asLiteral().getString().split(" "));
						System.out.println("types: "+types);
						
						String[] properties = solution.get("properties").asLiteral().getString().split(" ");
						List<Field> fields = getRelevantProperties(uri, types, properties); // Get Relevant Properties based on CouchDB
						if(fields.size() > 0) {
							for(Field field : fields) {
								System.out.print("FIELD: " + field.getName() + "\t");

								if(field.getValues() != null) {
									System.out.print("VALUES: " + field.getValues().toString());
								}
								else {
									System.out.print("VALUE: " + field.getValue());
								}

								System.out.println("\n\n");
							}
						}
						else {
							System.out.println("NO PROPERTIES FOUND");
						}
					}
				}
			}
			finally {
				queryExecution.close();
			}

		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*public static void main(String[] args) {
		new Genesis();
	}*/

	private List<Field> getRelevantProperties(String uri, List types, String[] properties) {
		List<Field> fields = new ArrayList();
		try {
			TreeMap<Float, String> propertyMap = new TreeMap();
			List<ExplorerProperties> explorerProperties = explorerDB.getViewRequestBuilder("explorer", "getProperties")
					.newRequest(Key.Type.STRING, ExplorerProperties.class)
					.keys(properties)
					.inclusiveEnd(true)
					.build().getResponse().getValues();


			for(ExplorerProperties property : explorerProperties) {
				// Check if the property matches one of the list of classes(types) found for the entity
				if(types.contains(property.getClassName())) {
					propertyMap.put(Float.parseFloat(property.getScore()), property.getProperty());
				}
			}

			if(propertyMap.size() > 0) {
				int count = 0;
				Iterator<Float> iterator = propertyMap.descendingKeySet().iterator(); // Sorts descending order
				String property_uris = "";
				while (count < MAX_FIELD_SIZE && iterator.hasNext()) {
					property_uris += "<" + propertyMap.get(iterator.next()) + "> ";
					count++;
				}

				String query = buildQuery("SELECT ?property_label (group_concat(distinct ?value;separator='__') as ?values) (group_concat(distinct ?value_label;separator='__') as ?value_labels) where {\n" +
						"VALUES ?property {" + property_uris + "}\n" +
						"<" + uri + "> ?property ?value . \n" +
						"?property rdfs:label ?property_label . FILTER(lang(?property_label)='en'). \n" +
						"OPTIONAL {?value rdfs:label ?value_label . FILTER(lang(?value_label) = 'en') }\n" +
						"} GROUP BY ?property_label");
				QueryExecution queryExecution = executeQuery(query);
				try {
					Iterator<QuerySolution> results = queryExecution.execSelect();
					while(results.hasNext()) {
						QuerySolution result = results.next();
						Field field = new Field();
						field.setName(result.get("property_label").asLiteral().getString());

						// If Value Label String is empty then we use Value String instead which means the value is a literal. So we are only taking the first element before space
						if(result.get("value_labels").asLiteral().getString().equals("")) {
							field.setValue(result.get("values").asLiteral().getString().split("__")[0]);
						}
						else {
							LinkedHashMap<String, String> map = new LinkedHashMap();
							String[] keyArray = result.get("values").asLiteral().getString().split("__");
							String[] valueArray = result.get("value_labels").asLiteral().getString().split("__");

							for(int index = 0; index < keyArray.length; index++) {
								map.put(keyArray[index], valueArray[index]);
							}
							field.setValues(map);
						}
						fields.add(field);
					}
					return fields;
				}
				finally {
					queryExecution.close();
					return fields;
				}
			}
			else {
				return fields;
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return fields;
	}

	// Class for storing CouchDB Query Response
	private class ExplorerProperties {
		private String className;
		private String property;
		private String score;

		public String getClassName() {
			return className;
		}

		public ExplorerProperties setClassName(String className) {
			this.className = className;
			return this;
		}

		public String getProperty() {
			return property;
		}

		public ExplorerProperties setProperty(String property) {
			this.property = property;
			return this;
		}

		public String getScore() {
			return score;
		}

		public ExplorerProperties setScore(String score) {
			this.score = score;
			return this;
		}
	}

	// Convenience class to better structure data from couchDB
	public static class Field {
		private String name;
		private String value;
		private LinkedHashMap<String, String> values = null;
		private boolean isShort = true;

		public Field() {

		}

		public Field(String name, String value) {
			this.name = name;
			this.value = value;
		}

		public String getName() {
			return name;
		}

		public Field setName(String name) {
			this.name = name;
			return this;
		}

		public String getValue() {
			return value;
		}

		public Field setValue(String value) {
			this.value = value;
			return this;
		}

		public Field(String name, String value, boolean isShort) {
			this.name = name;
			this.value = value;
			this.isShort = isShort;
		}

		public boolean isShort() {
			return isShort;
		}

		public Field setShort(boolean aShort) {
			isShort = aShort;
			return this;
		}

		public LinkedHashMap<String, String> getValues() {
			return values;
		}

		public Field setValues(LinkedHashMap<String, String> values) {
			this.values = values;
			return this;
		}

		public String getFieldValue(String platform) {
			return value;
		}
	}
}