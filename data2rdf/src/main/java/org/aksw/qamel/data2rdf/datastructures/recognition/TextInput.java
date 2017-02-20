package org.aksw.qamel.data2rdf.datastructures.recognition;

public class TextInput {
	private String input;

	private String type;

	public TextInput() {
	}

	public TextInput(String input, String type) {
		this.input = input;
		this.type = type;
	}

	public String getInput() {
		return input;
	}

	public String getType() {
		return type;
	}

	@Override
	public String toString() {
		return "TextInput [input=" + input + ", type=" + type + "]";
	}

}
