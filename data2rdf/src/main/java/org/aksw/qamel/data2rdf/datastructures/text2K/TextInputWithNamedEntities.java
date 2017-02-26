package org.aksw.qamel.data2rdf.datastructures.text2K;

import java.util.List;

import org.aksw.qamel.data2rdf.datastructures.disambiguation.NEDAnnotation;

public class TextInputWithNamedEntities {
	private String input;
	private List<NEDAnnotation> namedEntities;

	public TextInputWithNamedEntities() {
	}

	public TextInputWithNamedEntities(String input, List<NEDAnnotation> namedEntities) {
		this.input = input;
		this.namedEntities = namedEntities;
	}

	public String getInput() {
		return input;
	}

	public List<NEDAnnotation> getNamedEntities() {
		return namedEntities;
	}

	@Override
	public String toString() {
		return "TextInputWithNamedEntities [input=" + input + ", namedEntities=" + namedEntities + "]";
	}

}
