package org.aksw.qamel.OQA;

import org.json.simple.JSONObject;

public class Answer {
	private String mPropertyLabel;
    private Match mMatch;
    private JSONObject mBindingSet;
    private String mAnswer;
    private int mConfidence;

    public Answer(Match match, JSONObject next, String answer, String question, int confidence, String propertyLabel) {
        mMatch = match;
        mBindingSet = next;
        mAnswer = answer;
        mConfidence = confidence;
        mPropertyLabel = propertyLabel;
    }

    public String getAnswer() {
        return mAnswer;
    }

    public int getConfidence() {
        return mConfidence;
    }

    @Override
    public String toString() {
        return mMatch.getUri() + " " + (String)  mBindingSet.get("p") + " "
                + (String)  mBindingSet.get("o") + "[" + mConfidence + "] '" + mPropertyLabel + "'\n";
    }

    public static class Comparator implements java.util.Comparator<Answer> {

        @Override
        public int compare(Answer answer, Answer t1) {
            if (answer.getConfidence() > t1.getConfidence()) return -1;
            if (answer.getConfidence() < t1.getConfidence()) return 1;
            return 0;
        }
    }

}
