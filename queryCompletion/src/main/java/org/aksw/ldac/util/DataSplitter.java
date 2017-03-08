package org.aksw.ldac.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataSplitter {
	Logger log = LoggerFactory.getLogger(this.getClass());
	private ArrayList<StringBuilder> builder;

	public DataSplitter(String data, int parts) {
		this.builder = new ArrayList<StringBuilder>();
		for (int j = 0; j < parts; j++) {
			builder.add(new StringBuilder());
		}
		try {
			BufferedReader br = new BufferedReader(new FileReader(data));
			while (br.ready()) {
				for (int i = 0; i < parts; ++i) {
					if (br.ready()) {
						builder.get(i).append(br.readLine());
						builder.get(i).append("\n");
					}
				}
			}
			br.close();
		} catch (IOException e) {
			log.error(e.getLocalizedMessage());
		}
	}

	public Pair<InputStream, InputStream> getPartsOfData(int iteration) {
		try {
			StringBuilder left = new StringBuilder();
			StringBuilder right = new StringBuilder();
			for (int i = 0; i < builder.size(); ++i) {
				if (i == iteration) {
					left.append(builder.get(i).toString());
				} else {
					right.append(builder.get(i).toString());
				}
			}
			ByteArrayInputStream leftStream = new ByteArrayInputStream(left.toString().getBytes("UTF-8"));
			ByteArrayInputStream rightStream = new ByteArrayInputStream(right.toString().getBytes("UTF-8"));
			return new ImmutablePair<InputStream, InputStream>(leftStream, rightStream);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

}
