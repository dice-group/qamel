package org.aksw.qamel.data2rdf.datastructures.text2K;

public class TextInputWithNamedEntities {
	private String input;
	private NamedEntities namedEntities;

	public TextInputWithNamedEntities() {
	}


	public TextInputWithNamedEntities(String input, NamedEntities namedEntities) {
		this.input = input;
		this.namedEntities = namedEntities;
	}

	public String getInput() {
		return input;
	}
	public NamedEntities getNamedEntities() {
		return namedEntities;
	}

	@Override
	public String toString() {
		return "TextInputWithNamedEntities [input=" + input + ", namedEntities=" + namedEntities + "]";
	}

}
