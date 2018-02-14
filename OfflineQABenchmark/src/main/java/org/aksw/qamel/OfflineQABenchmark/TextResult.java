package org.aksw.qamel.OfflineQABenchmark;

public class TextResult extends QAResult {
    private static final String TAG = TextResult.class.getSimpleName();

    private String mData;

    public TextResult(String question, String text) {
        super(question);
        mData = text;
    }

    @Override
    public String toString() {
        return mData;
    }
}
