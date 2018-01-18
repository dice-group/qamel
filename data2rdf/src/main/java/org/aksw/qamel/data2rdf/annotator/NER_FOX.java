package org.aksw.qamel.data2rdf.annotator;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.jena.ext.com.google.common.base.Joiner;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFReader;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.rdf.model.impl.ResourceImpl;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NER_FOX {
	static Logger log = LoggerFactory.getLogger(NER_FOX.class);

	// TODO write unit test for each language
	public static void main(String args[]) throws Exception {
		NER_FOX fox = new NER_FOX();

		String sentence = "Katie Holmes got divorced from Tom Cruise in Deutschland.";
		Map<String, List<Entity>> list;
		try {
			list = fox.getEntities(sentence, "de");
			for (String key : list.keySet()) {
				System.out.println(key);
				for (Entity entity : list.get(key)) {
					System.out.println("\t" + entity.label + " ->" + entity.type);
					for (String r : entity.posTypesAndCategories) {
						System.out.println("\t\tpos: " + r);
					}
					for (String r : entity.uris) {
						System.out.println("\t\turi: " + r);
					}
				}
			}
		} catch (ParseException | IOException e) {
			e.printStackTrace();
		}
	}

	private String requestURL = "http://fox-demo.aksw.org/fox";
	private String outputFormat = "N-Triples";
	private String taskType = "NER";

	// TODO change to unit test

	private String inputType = "text";

	private String doTASK(String inputText, String lang) throws Exception {

		String urlParameters = "type=" + inputType;
		urlParameters += "&task=" + taskType;
		if (lang.equals("de")) {
			urlParameters += "&lang=" + "de";
		} else if (lang.equals("en")) {
			// do nothing
		} else {
			throw new Exception("Language tag unknow");
		}

		urlParameters += "&output=" + outputFormat;
		urlParameters += "&input=" + URLEncoder.encode(inputText, "UTF-8");
		return POST(urlParameters, requestURL);
		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.aksw.hawk.nlp.NERD_module#getEntities(java.lang.String)
	 */
	public Map<String, List<Entity>> getEntities(String question, String lang) throws Exception {

		HashMap<String, List<Entity>> tmp = new HashMap<String, List<Entity>>();
		String foxJSONOutput = doTASK(question, lang);

		JSONParser parser = new JSONParser();
		JSONObject jsonArray = (JSONObject) parser.parse(foxJSONOutput);
		String output = URLDecoder.decode((String) jsonArray.get("output"), "UTF-8");

		String baseURI = "http://dbpedia.org";
		Model model = ModelFactory.createDefaultModel();
		RDFReader r = model.getReader("N3");
		r.read(model, new StringReader(output), baseURI);

		ResIterator iter = model.listSubjects();
		ArrayList<Entity> tmpList = new ArrayList<>();
		while (iter.hasNext()) {
			Resource next = iter.next();
			StmtIterator statementIter = next.listProperties();
			Entity ent = new Entity();
			while (statementIter.hasNext()) {
				Statement statement = statementIter.next();
				String predicateURI = statement.getPredicate()
				                               .getURI();
				if (predicateURI.equals("http://www.w3.org/2000/10/annotation-ns#body")) {
					ent.label = statement.getObject()
					                     .asLiteral()
					                     .getString();
				} else if (predicateURI.equals("http://ns.aksw.org/scms/means")) {
					String uri = statement.getObject()
					                      .asResource()
					                      .getURI();
					String encode = uri.replaceAll(",", "%2C");
					ResourceImpl e = new ResourceImpl(encode);
					ent.uris.add(e.getURI()
					              .toString());
				} else if (predicateURI.equals("http://www.w3.org/1999/02/22-rdf-syntax-ns#type")) {
					ent.posTypesAndCategories.add(statement.getObject()
					                                       .asResource()
					                                       .getURI()
					                                       .toString());
				} else if (predicateURI.equals("http://ns.aksw.org/scms/beginIndex")) {
					ent.beginIndex = statement.getObject()
					                          .asLiteral()
					                          .getInt();
				} else if (predicateURI.equals("http://ns.aksw.org/scms/endIndex")) {
					ent.endIndex = statement.getObject()
					                        .asLiteral()
					                        .getInt();
				}
			}
			tmpList.add(ent);
		}
		tmp.put("en", tmpList);

		if (!tmp.isEmpty()) {
			log.debug("\t" + Joiner.on("\n")
			                       .join(tmp.get("en")));
		}
		return tmp;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.aksw.hawk.nlp
	 */
	private String POST(String urlParameters, String requestURL) throws MalformedURLException, IOException, ProtocolException {
		URL url = new URL(requestURL);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("POST");
		connection.setDoOutput(true);
		connection.setDoInput(true);
		connection.setUseCaches(false);
		connection.setRequestProperty("Accept", "application/json");
		connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
		connection.setRequestProperty("Content-Length", String.valueOf(urlParameters.length()));

		DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
		wr.writeBytes(urlParameters);
		wr.flush();

		InputStream inputStream = connection.getInputStream();
		InputStreamReader in = new InputStreamReader(inputStream);
		BufferedReader reader = new BufferedReader(in);

		StringBuilder sb = new StringBuilder();
		while (reader.ready()) {
			sb.append(reader.readLine());
		}

		wr.close();
		reader.close();
		connection.disconnect();

		return sb.toString();
	}

}
