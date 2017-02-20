package org.aksw.qamel.data2rdf.datastructures.text2K;

import java.util.List;

public class Triples {
	private final List<Triple> output;

	public Triples(List<Triple> output) {
		super();
		this.output = output;
	}

	public List<Triple> getOutput() {
		return output;
	}

	@Override
	public String toString() {
		return "Triples [output=" + output + "]";
	}
}
