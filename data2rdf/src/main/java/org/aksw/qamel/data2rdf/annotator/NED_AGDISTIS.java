package org.aksw.qamel.data2rdf.annotator;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.aksw.qamel.data2rdf.datastructures.disambiguation.NEDAnnotation;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * URI Disambiguation using AGDISTIS https://github.com/AKSW/AGDISTIS
 * 
 * @author r.usbeck
 * 
 */
public class NED_AGDISTIS {
	/**
	 * testing main
	 * 
	 * @param args
	 * @throws ParseException
	 * @throws IOException
	 */
	// TODO change to unit test
	public static void main(String[] args) throws ParseException, IOException {
		NED_AGDISTIS post = new NED_AGDISTIS();
		String subjectString = "Tom Cruise";
		String objectString = "Katie Holmes";

		String preAnnotatedText = "<entity>" + subjectString + "</entity><entity>" + objectString + "</entity>";

		List<NEDAnnotation> results = post.runDisambiguation(preAnnotatedText);
		for (NEDAnnotation namedEntity : results) {
			System.out.println(namedEntity);
		}

	}

	/**
	 * 
	 * @param inputText
	 *            with encoded entities,e.g.,
	 *            "<entity> Barack </entity> meets <entity>Angela</entity>"
	 * @return map of string to disambiguated URL
	 * @throws ParseException
	 * @throws IOException
	 */
	public List<NEDAnnotation> runDisambiguation(String inputText) throws ParseException, IOException {
		String urlParameters = "text=" + URLEncoder.encode(inputText, "UTF-8");
		urlParameters += "&type=agdistis";

		// change this URL to http://139.18.2.164:8080/AGDISTIS_ZH to use
		// chinese endpoint
		// old endpoint with DBpedia 2014 URL url = new
		// URL("http://139.18.2.164:8080/AGDISTIS");
		// new endpoint with DBpedia 2016-04 temporarily
		// German: http://139.18.2.164:4447/AGDISTIS/
		// English:http://139.18.2.164:4445/AGDISTIS/
		URL url = new URL("http://139.18.2.164:4445/AGDISTIS/");
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("GET");
		connection.setDoOutput(true);
		connection.setDoInput(true);
		connection.setUseCaches(false);
		connection.setRequestProperty("Accept", "application/json");
		connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
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

		String agdistis = sb.toString();

		JSONParser parser = new JSONParser();
		JSONArray resources = (JSONArray) parser.parse(agdistis);

		List<NEDAnnotation> annotations = new ArrayList<NEDAnnotation>();
		for (Object res : resources.toArray()) {
			JSONObject next = (JSONObject) res;
			String namedEntity = (String) next.get("namedEntity");
			String disambiguatedURL = (String) next.get("disambiguatedURL");
			long begin = (long) next.get("start");
			long offset = (long) next.get("offset");
			long end = begin + offset;
			NEDAnnotation tmpEntity = new NEDAnnotation(namedEntity, begin, end, offset, disambiguatedURL);
			annotations.add(tmpEntity);
		}
		return annotations;
	}
}