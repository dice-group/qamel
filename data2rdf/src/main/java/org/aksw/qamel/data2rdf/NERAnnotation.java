package org.aksw.qamel.data2rdf;

import java.util.List;

public class NERAnnotation {
	private final int id;
	private final String body;
	private final List<String> type;
	private final int beginIndex;
	private final int endIndex;
	public NERAnnotation(int id, String body, List<String> type, int beginIndex, int endIndex) {
	    this.id = id;
	    this.body = body;
	    this.type = type;
	    this.beginIndex = beginIndex;
	    this.endIndex = endIndex;
    }
	public int getBeginIndex() {
		return beginIndex;
	}
	public String getBody() {
		return body;
	}
	public int getEndIndex() {
		return endIndex;
	}
	public int getId() {
		return id;
	}
	public List<String> getType() {
		return type;
	}
	@Override
    public String toString() {
	    return "Annotation [id=" + id + ", body=" + body + ", type=" + type + ", beginIndex=" + beginIndex + ", endIndex=" + endIndex + "]";
    }
	
	
}
