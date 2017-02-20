package org.aksw.qamel.data2rdf;

import java.util.List;

public class TextWithRecognizedEntities {
	private final List<Annotation> output;

	private final Context context;
	public TextWithRecognizedEntities(List<Annotation> output, Context context) {
		this.output = output;
		this.context = context;
	}

	public Context getContext() {
		return context;
	}

	public List<Annotation> getOutput() {
		return output;
	}

	@Override
    public String toString() {
	    return "TextWithRecognizedEntities [output=" + output + ", context=" + context + "]";
    }

}
