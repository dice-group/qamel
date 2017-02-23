package org.aksw.qamel.data2rdf.annotator;

import java.util.ArrayList;
import java.util.List;

//TODO remove that class and replace it by NERAnnotation
public class Entity {

	public List<String> uris = new ArrayList<String>();
	public List<String> posTypesAndCategories = new ArrayList<String>();
	public String type;
	public String label;
	public int beginIndex;
	public int endIndex;

}