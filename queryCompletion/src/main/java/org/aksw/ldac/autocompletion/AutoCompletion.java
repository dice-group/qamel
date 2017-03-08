package org.aksw.ldac.autocompletion;

import java.io.InputStream;

public interface AutoCompletion {

	void setTrainingQueries(InputStream training);

	String getFullQuery(String substring);

}
