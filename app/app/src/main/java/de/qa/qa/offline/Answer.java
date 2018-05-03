package de.qa.qa.offline;

import org.eclipse.rdf4j.query.BindingSet;

public class Answer {
    private String mPropertyLabel;
    private Match mMatch;
    private BindingSet mBindingSet;
    private String mAnswer;
    private String mQuestion;
    private int mConfidence;

    public Answer(Match match, BindingSet bindingSet, String answer, String question, int confidence, String propertyLabel) {
        mMatch = match;
        mBindingSet = bindingSet;
        mAnswer = answer;
        mQuestion = question;
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
        return mMatch.getUri() + " " + mBindingSet.getValue("p").stringValue() + " "
                + mBindingSet.getValue("o").stringValue() + "[" + mConfidence + "] '" + mPropertyLabel + "'";
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