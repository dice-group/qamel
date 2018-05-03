package de.qa.qa.result;

/**
 * Not really a result, but can be used to show the user that there is no result.
 */
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