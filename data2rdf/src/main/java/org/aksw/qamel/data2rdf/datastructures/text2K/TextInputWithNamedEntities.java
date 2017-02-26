package org.aksw.qamel.data2rdf.datastructures.text2K;

import java.util.List;

import org.aksw.qamel.data2rdf.datastructures.disambiguation.NEDAnnotation;

public class TextInputWithNamedEntities {
	private String input;
	private String lang;
	private List<NEDAnnotation> namedEntities;

	public TextInputWithNamedEntities() {
	}

	public TextInputWithNamedEntities(String input, String lang, List<NEDAnnotation> namedEntities) {
		this.input = input;
		this.lang = lang;
		this.namedEntities = namedEntities;
	}

	public String getInput() {
		return input;
	}

	public String getLang() {
		return lang;
	}

	public List<NEDAnnotation> getNamedEntities() {
		return namedEntities;
	}

	@Override
	public String toString() {
		return "TextInputWithNamedEntities [input=" + input + ", lang=" + lang + ", namedEntities=" + namedEntities + "]";
	}

}
