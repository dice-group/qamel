package org.aksw.qamel.data2rdf;


public class NEDAnnotation {
	private final String namedEntity;
	private final String disambiguatedURL;
	private final int start;
	private final int end;
	private final int offset;

	public NEDAnnotation(String namedEntity, int start, int end, int offset, String disambiguatedURL) {
		this.namedEntity = namedEntity;
		this.disambiguatedURL = disambiguatedURL;
		this.start = start;
		this.end = end;
		this.offset = offset;
	}

	public String getDisambiguatedURL() {
		return disambiguatedURL;
	}

	public int getEnd() {
		return end;
	}

	public String getNamedEntity() {
		return namedEntity;
	}

	public int getOffset() {
		return offset;
	}

	public int getStart() {
		return start;
	}

	@Override
	public String toString() {
		return "NEDAnnotation [namedEntity=" + namedEntity + ", disambiguatedURL=" + disambiguatedURL + ", start=" + start + ", end=" + end + ", offset=" + offset + "]";
	}

}
