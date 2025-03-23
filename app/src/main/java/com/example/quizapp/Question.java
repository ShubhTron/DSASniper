package com.example.quizapp;

import java.util.List;

public class Question {
    private String question;
    private List<String> options;
    private String answer;
    private String difficulty;

    // ✅ Constructor
    public Question(String question, List<String> options, String answer, String difficulty) {
        this.question = question;
        this.options = options;
        this.answer = answer;
        this.difficulty = difficulty;
    }

    // ✅ Getters
    public String getQuestion() { return question; }
    public List<String> getOptions() { return options; }
    public String getAnswer() { return answer; }
    public String getCorrectAnswer() { return answer; } // Duplicate, but keeping for backward compatibility
    public String getDifficulty() { return difficulty; }

    // ✅ Setters (Optional, in case you need to modify data dynamically)
    public void setQuestion(String question) { this.question = question; }
    public void setOptions(List<String> options) { this.options = options; }
    public void setAnswer(String answer) { this.answer = answer; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

    // ✅ Debugging Helper Method
    @Override
    public String toString() {
        return "Question{" +
                "question='" + question + '\'' +
                ", options=" + options +
                ", answer='" + answer + '\'' +
                ", difficulty='" + difficulty + '\'' +
                '}';
    }
}
