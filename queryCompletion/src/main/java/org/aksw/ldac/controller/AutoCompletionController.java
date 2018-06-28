package org.aksw.ldac.controller;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import org.aksw.ldac.autocompletion.AC_Lucene;
import org.aksw.ldac.autocompletion.AC_materializedLinkedData;
import org.aksw.ldac.autocompletion.AutoCompletion;
import org.aksw.ldac.eval.Evaluation;
import org.aksw.ldac.util.DataSplitter;
import org.aksw.ldac.util.LatexWriter;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AutoCompletionController {
	Logger log = LoggerFactory.getLogger(AutoCompletionController.class);
	private String[] datasets;

	public static void main(String args[]) throws FileNotFoundException {

		AutoCompletionController controller = new AutoCompletionController();
		controller.setDatasets(new String[] { "src/main/resources/file1.txt", "src/main/resources/file2.txt" });
		controller.run();
	}

	private void run() throws FileNotFoundException {
		HashMap<String, HashMap<String, Double>> approachDatasetValue = new HashMap<String, HashMap<String, Double>>();

		for (String data : datasets) {
			DataSplitter dataSplitter = new DataSplitter(data, 10);
			// new AC_Lucene(), new AC_PrefixTrie()
			for (AutoCompletion ac : new AutoCompletion[] { new AC_Lucene() }) {
				ArrayList<Double> aucs = crossValidate(dataSplitter, ac);
				saveResult(approachDatasetValue, data, ac, aucs);
				log.info("Approach: " + ac.toString() + " Data: " + data + " Average AUC = " + average(aucs));
			}
		}
		LatexWriter.writeLatex(approachDatasetValue, datasets);
	}

	private ArrayList<Double> crossValidate(DataSplitter ds, AutoCompletion ac) {
		ArrayList<Double> aucs = new ArrayList<Double>();
		for (int iteration = 0; iteration < 10; iteration++) {
			Pair<InputStream, InputStream> crossValidData = ds.getPartsOfData(iteration);

			ac.setTrainingQueries(crossValidData.getRight());

			Evaluation ev = new Evaluation();
			ev.setTestQueries(crossValidData.getLeft());
			ev.setAutoCompletionAlgortihm(ac);
			aucs.add(ev.getAreaUnderCurveBetweenLengthOfQueryAndPercentCorrectQueries());
		}
		return aucs;
	}

	private void saveResult(HashMap<String, HashMap<String, Double>> approachDatasetValue, String dataset, AutoCompletion ac, ArrayList<Double> aucs) {
		if (approachDatasetValue.containsKey(ac.toString())) {
			HashMap<String, Double> x = approachDatasetValue.get(ac.toString());
			x.put(dataset, average(aucs));
		} else {
			HashMap<String, Double> tmp = new HashMap<String, Double>();
			tmp.put(dataset, average(aucs));
			approachDatasetValue.put(ac.toString(), tmp);
		}
	}

	private double average(ArrayList<Double> aucs) {
		double sum = 0;
		for (Double d : aucs) {
			sum += d;
		}
		return sum / aucs.size();
	}

	private void setDatasets(String[] strings) {
		this.datasets = strings;

	}
}
