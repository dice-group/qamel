package org.aksw.qamel.OfflineQA;

public class TextResult extends QAResult {
    private static final String TAG = TextResult.class.getSimpleName();

    public String getmData() {
		return mData;
	}

	public void setmData(String mData) {
		this.mData = mData;
	}

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