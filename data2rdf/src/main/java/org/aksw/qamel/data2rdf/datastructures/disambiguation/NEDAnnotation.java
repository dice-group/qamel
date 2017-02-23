package org.aksw.qamel.data2rdf.datastructures.disambiguation;

public class NEDAnnotation {
	private  String namedEntity;
	private  String disambiguatedURL;
	private  long start;
	private  long end;
	private  long offset;

	public NEDAnnotation() {
	}

	public NEDAnnotation(String namedEntity, long start, long end, long offset, String disambiguatedURL) {
		this.namedEntity = namedEntity;
		this.disambiguatedURL = disambiguatedURL;
		this.start = start;
		this.end = end;
		this.offset = offset;
	}

	public String getDisambiguatedURL() {
		return disambiguatedURL;
	}

	public long getEnd() {
		return end;
	}

	public String getNamedEntity() {
		return namedEntity;
	}

	public long getOffset() {
		return offset;
	}

	public long getStart() {
		return start;
	}

	@Override
	public String toString() {
		return "NEDAnnotation [namedEntity=" + namedEntity + ", disambiguatedURL=" + disambiguatedURL + ", start=" + start + ", end=" + end + ", offset=" + offset + "]";
	}

}
