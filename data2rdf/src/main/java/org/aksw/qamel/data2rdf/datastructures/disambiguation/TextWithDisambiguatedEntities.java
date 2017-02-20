package org.aksw.qamel.data2rdf.datastructures.disambiguation;

import java.util.List;

public class TextWithDisambiguatedEntities {
	private final List<NEDAnnotation> output;

	public TextWithDisambiguatedEntities(List<NEDAnnotation> output) {
		this.output = output;
	}

	public List<NEDAnnotation> getOutput() {
		return output;
	}

	@Override
	public String toString() {
		return "TextWithDisambiguatedEntities [output=" + output + "]";
	}

}
