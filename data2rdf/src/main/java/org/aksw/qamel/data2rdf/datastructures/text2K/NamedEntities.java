package org.aksw.qamel.data2rdf.datastructures.text2K;

import java.util.List;

import org.aksw.qamel.data2rdf.datastructures.disambiguation.NEDAnnotation;

public class NamedEntities {
	private  List<NEDAnnotation> output;

	public NamedEntities() {
	}

	public NamedEntities(List<NEDAnnotation> output) {
		this.output = output;
	}
	public List<NEDAnnotation> getOutput() {
		return output;
	}

	@Override
	public String toString() {
		return "NamedEntities [output=" + output + "]";
	}
}
