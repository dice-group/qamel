package org.aksw.ldac.eval;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.aksw.ldac.autocompletion.AutoCompletion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Evaluation {
	Logger log = LoggerFactory.getLogger(Evaluation.class);
	private List<String> testQueries;
	private AutoCompletion ac;

	public double getAreaUnderCurveBetweenLengthOfQueryAndPercentCorrectQueries() {
		HashMap<String, boolean[]> queries = new HashMap<String, boolean[]>();
		for (String query : testQueries) {
			if (query.length() > 0) {
				log.debug(query);
				boolean[] q = new boolean[query.length()];
				for (int i = 1; i <= query.length(); ++i) {
					String substring = query.substring(0, i);
					String guessedQuery = ac.getFullQuery(substring);
					log.debug("\t" + substring + "->" + guessedQuery);
					if (query.equals(guessedQuery)) {
						q[i - 1] = true;
					} else {
						q[i - 1] = false;
					}
				}
				queries.put(query, q);
			}
		}
		for (String q : queries.keySet()) {
			StringBuilder sb = new StringBuilder();
			for (boolean b : queries.get(q)) {
				sb.append(b + "\t");
			}
			log.debug(sb.toString());
		}
		double areaUnderCurve = 0;
		double samplingRate = 100000;
		for (double i = 0; i < samplingRate; ++i) {
			double percentage = i / samplingRate;
			double areaUnderSamplePoint = 0;
			for (String query : queries.keySet()) {
				boolean[] tmp = queries.get(query);
				if (tmp[(int) (percentage * query.length())]) {
					areaUnderSamplePoint++;
				}
			}
			areaUnderCurve += areaUnderSamplePoint / queries.size();
		}
		return areaUnderCurve / samplingRate;
	}

	public void setAutoCompletionAlgortihm(AutoCompletion ac) {
		this.ac = ac;

	}

	public void setTestQueries(InputStream test) {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(test));
			ArrayList<String> tmp = new ArrayList<String>();
			while (br.ready()) {
				tmp.add(br.readLine());
			}
			br.close();
			this.testQueries = tmp;
		} catch (IOException e) {
			log.error(e.getLocalizedMessage());
		}

	}

	public void setTestQueries(List<String> test) {
		this.testQueries = test;
	}
}
