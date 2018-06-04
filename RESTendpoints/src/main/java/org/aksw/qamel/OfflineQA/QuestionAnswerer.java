package org.aksw.qamel.OfflineQA;

public interface QuestionAnswerer {
    /**
     * Answers a question
     * @param question the question as entered by the user
     * @return all answers which were found for this question by this QuestionAnswerer
     */
    QAResult[] answerQuestion(String question);
}
