package org.aksw.ldac.nlp;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.aksw.ldac.util.SparqlEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Segmenter {

	SparqlEndpoint sparql;
	static Logger log = LoggerFactory.getLogger(Segmenter.class);

	public Segmenter(SparqlEndpoint sparql) {
		this.sparql = sparql;
	}

	public static void main(String[] args) throws UnsupportedEncodingException {

		ArrayList<String> testQueries = new ArrayList<String>();
		testQueries.add("Flamingo");
		testQueries.add("Hotel Leipzig");
		testQueries.add("Hotel Berlin mit WLAN");
		Segmenter seg = new Segmenter(new SparqlEndpoint());
		for (String q : testQueries) {
			for (List<String> list : seg.getSegmentsRecursively(q)) {
				String segString = "";
				for (String item : list) {
					segString += item + " | ";
				}
				log.debug(segString);
			}
		}
	}

	public List<List<String>> getSegmentsRecursively(String substring) {
		List<List<String>> segments = new ArrayList<List<String>>();
		String[] split = substring.split("\\s+");
		for (int i = 1; i < split.length; i++) {
			String segmentsLeft = segments(split, 0, i);
			String segmentsRights = segments(split, i, split.length);
			List<List<String>> segmentsRecursively = getSegmentsRecursively(segmentsRights);
			for (List<String> segList : segmentsRecursively) {
				segList.add(0, segmentsLeft);
				segments.add(segList);
			}
		}
		segments.add(new ArrayList<String>(Arrays.asList(substring)));
		return segments;
	}

	private String segments(String[] split, int i, int j) {
		String tmp = "";
		for (int x = i; x < j; x++) {
			tmp += split[x] + " ";
		}
		return tmp.trim();
	}

}
