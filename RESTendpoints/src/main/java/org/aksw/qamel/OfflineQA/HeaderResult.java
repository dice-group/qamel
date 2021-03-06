package org.aksw.qamel.OfflineQA;

public class HeaderResult extends QAResult {
    private static final String TAG = HeaderResult.class.getSimpleName();

    public HeaderResult(String question) {
        super(question);
    }

    @Override
    public String toString() {
        return getQuestion();
    }
}