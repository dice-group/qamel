package org.aksw.qamel.data2rdf.datastructures.recognition;

public class TextInput {
	private String input;
	private String lang;
	private String type;

	public TextInput() {
	}

	public TextInput(String input, String lang, String type) {
		super();
		this.input = input;
		this.lang = lang;
		this.type = type;
	}

	public String getInput() {
		return input;
	}

	public String getLang() {
		return lang;
	}

	public String getType() {
		return type;
	}

	@Override
	public String toString() {
		return "TextInput [input=" + input + ", lang=" + lang + ", type=" + type + "]";
	}

}
