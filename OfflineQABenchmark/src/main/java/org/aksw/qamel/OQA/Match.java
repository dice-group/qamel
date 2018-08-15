package org.aksw.qamel.OQA;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import info.debatty.java.stringsimilarity.Levenshtein;

public class Match {
	
	public static final int TYPE_UNKNOWN = 0x0;
    public static final int TYPE_PROPERTY = 0x1;
    public static final int TYPE_THING = 0x2;
    private int mDistance;
    private String mUri;
    private String mLabel;
    private List<String> mWords;
    private String mQuestion;
    private int mConfidence;

    public Match(String uri, String question, String label, String word) {
        mUri = uri;
        mLabel = label;
        mWords = new ArrayList<>();
        mWords.add(word);
        mQuestion = question;
        Levenshtein levenshtein = new Levenshtein();
        mDistance = (int) levenshtein.distance(label, word);
        mConfidence = 0;
    }

    public int getDistance() {
        return mDistance;
    }

    public String getUri() {
        return mUri;
    }

    public int getConfidence() {
        return mConfidence;
    }

    public String getLabel() {
        return mLabel;
    }

    public String getWordsString() {
        return Arrays.toString(mWords.toArray());
    }

    public List<String> getWords() {
        return mWords;
    }

    public String getQuestion() {
        return mQuestion;
    }

    public void addWord(String word) {
        mWords.add(word);
        StringBuilder builder = new StringBuilder();
        for (String w : mWords) {
            builder.append(w).append(" ");
        }
        String newWord = builder.toString();
        mDistance = (int) new Levenshtein().distance(newWord, mLabel);
    }

    public int getWordsLength() {
        int l = 0;
        for (String w : mWords) {
            l += w.length();
        }
        return l;
    }

    public static class Comparator implements java.util.Comparator<Match> {

        public int compare(Match match, Match t1) {
            if (match.getWords().size() > t1.getWords().size()) return -1;
            if (match.getWords().size() < t1.getWords().size()) return 1;
            if (match.getDistance() < t1.getDistance()) return -1;
            if (match.getDistance() > t1.getDistance()) return 1;
            if (match.getLabel().length() > t1.getLabel().length()) return -1;
            if (match.getLabel().length() < t1.getLabel().length()) return 1;
            return 0;
        }
    }

	@Override
	public String toString() {
		return "Match [mLabel=" + mLabel + "]";
	}

}
