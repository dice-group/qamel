package org.aksw.qamel.data2rdf;

public class Context {
	@SuppressWarnings("unused")
    private final String ann;

	@SuppressWarnings("unused")
	private final String scmsann;
	public Context(String ann, String scmsann) {
		this.ann = ann;
		this.scmsann = scmsann;
	}

	public String getAnn() {
		return "http://www.w3.org/2000/10/annotation-ns#";
	}

	public String getScmsann() {
		return "http://ns.aksw.org/scms/annotations/";
	}

	@Override
    public String toString() {
	    return "Context [ann=" + ann + ", scmsann=" + scmsann + "]";
    }
}
