package org.aksw.qamel.OQA;

public class QAResult {
	  private String mData;
	    private String mQuestion;

	    public QAResult(String question) {
	        mQuestion = question;
	    }

	    public QAResult(String question, String data) {
	        this(question);
	        mData = data;
	    }

	    @Override
	    public String toString() {
	        return mData;
	    }

	    public String getQuestion() {
	        return mQuestion;
	    }

}
