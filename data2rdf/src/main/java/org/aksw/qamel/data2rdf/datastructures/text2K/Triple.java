package org.aksw.qamel.data2rdf.datastructures.text2K;

public class Triple {
	private final String subject;
	private final String property;
	private final String object;
	private final double confidence;

	public Triple(String subject, String property, String object, double confidence) {
		super();
		this.subject = subject;
		this.property = property;
		this.object = object;
		this.confidence = confidence;
	}

	public double getConfidence() {
		return confidence;
	}

	public String getObject() {
		return object;
	}

	public String getProperty() {
		return property;
	}

	public String getSubject() {
		return subject;
	}

	@Override
	public String toString() {
		return "Triple [subject=" + subject + ", property=" + property + ", object=" + object + ", confidence=" + confidence + "]";
	}
}
