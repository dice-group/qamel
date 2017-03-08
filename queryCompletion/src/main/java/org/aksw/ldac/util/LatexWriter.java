package org.aksw.ldac.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;

public class LatexWriter {
	public static void writeLatex(HashMap<String, HashMap<String, Double>> approachDatasetValue, String[] datasets) {
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter("Autocompletion.tex"));
			bw.write("\\begin{table*}[htb!]");
			bw.newLine();
			bw.write("\\centering");
			bw.newLine();
			bw.write("\\caption{Evaluation of Linked Data Approaches.}");
			bw.newLine();
			bw.write("\\begin{tabular}{cccc}");
			bw.newLine();
			bw.write("\\toprule");
			bw.newLine();

			int i = 0;
			bw.write("&");
			for (String approach : approachDatasetValue.keySet()) {
				bw.write("\\textbf{" + approach + "} ");
				i++;
				if (i < approachDatasetValue.keySet().size()) {
					bw.write("&");
				} else {
					bw.write("\\\\");
				}
			}
			bw.newLine();
			bw.write("\\midrule");
			bw.newLine();

			for (String dataset : datasets) {
				i = 0;
				bw.write(dataset + "&");
				for (String approach : approachDatasetValue.keySet()) {
					Double value = approachDatasetValue.get(approach).get(dataset);
					DecimalFormat df = new DecimalFormat("#.##");
					try {
						bw.write(df.format(value));
					} catch (IllegalArgumentException e) {
						bw.write("?");
					}
					i++;
					if (i < approachDatasetValue.keySet().size()) {
						bw.write("&");
					} else {
						bw.write("\\\\");
					}
				}
				bw.newLine();
			}
			bw.write("\\bottomrule");
			bw.newLine();
			bw.write("\\end{tabular}");
			bw.newLine();
			bw.write("\\end{table*}");

			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
