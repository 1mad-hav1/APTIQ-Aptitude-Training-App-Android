package com.example.aptitude;

public class Question {
    private final String questionText;
    private final String correctAnswer;
    private final String explanation;

    public Question(String questionText, String correctAnswer, String explanation) {
        this.questionText = questionText;
        this.correctAnswer = correctAnswer;
        this.explanation = explanation;
    }

    public String getQuestionText() {
        return questionText;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public String getExplanation() {
        return explanation;
    }
}
