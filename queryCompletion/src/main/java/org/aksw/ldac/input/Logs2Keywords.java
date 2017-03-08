package org.aksw.ldac.input;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

public class Logs2Keywords {

	public static void main(String args[]) throws IOException, URISyntaxException {

		String logFile = "/data/r.usbeck/bluekiwi_logs/all.txt";
		BufferedReader br = new BufferedReader(new FileReader(logFile));
		Logs2Keywords l2k = new Logs2Keywords();

		BufferedWriter bw = new BufferedWriter(new FileWriter("/data/r.usbeck/bluekiwi_logs/all_cleaned.txt"));
		int c = 0;
		while (br.ready()) {
			String logLine = br.readLine();
			if (logLine.contains("GET http://www.bluekiwi.de/web/search?it=")) {
				if (!isBot(logLine)) {
					String keywordQuery = l2k.transformLogToQuery(logLine);
					bw.write(keywordQuery);
					bw.newLine();
				}
			}
			if (c % 100000 == 0) {
				System.out.println((double)c/(double)62522794+" %");
				bw.flush();
				System.gc();
			}
			++c;
		}
		br.close();
		bw.close();
	}

	private static boolean isBot(String logLine) {
		String userAgent = logLine.substring(logLine.indexOf("user-agent=") + "user-agent=".length());
		if (userAgent.contains("Bot") || userAgent.contains("Googlebot"))
			return true;
		// System.out.println(userAgent);
		return false;
	}

	public String transformLogToQuery(String log) throws MalformedURLException, URISyntaxException {
		String substring = log.substring(log.lastIndexOf("GET ") + "GET ".length());
		String url = substring.split(" ")[0];
		List<NameValuePair> params = URLEncodedUtils.parse(new URI(url), "UTF-8");
		for (NameValuePair param : params) {
			if (param.getName().equals("it"))
				return param.getValue();
		}
		throw new MalformedURLException("Query Param not found");
	}

}
