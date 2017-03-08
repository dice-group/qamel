package org.aksw.ldac.input;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;

public class QueryReader {
	Logger log = org.slf4j.LoggerFactory.getLogger(QueryReader.class);
	private String file;

	public QueryReader(String file) {
		this.file = file;
	}

	public List<String> getModel() {
		ArrayList<String> queries = new ArrayList<String>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			while (br.ready()) {
				queries.add(br.readLine());
			}
			br.close();
		} catch (IOException e) {
			log.error(e.getLocalizedMessage());
		}
		return queries;
	}

}
