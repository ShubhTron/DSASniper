package com.example.quizapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;

public class QuizManager {
    private SharedPreferences sharedPreferences;
    private List<Question> questionList;

    public QuizManager(Context context, List<Question> questions) {
        sharedPreferences = context.getSharedPreferences("QuizPrefs", Context.MODE_PRIVATE);
        this.questionList = (questions != null) ? questions : new ArrayList<>();

        // ✅ Debug log to check if questions are loaded
        if (this.questionList.isEmpty()) {
            Log.e("QuizManager", "No questions loaded! Check JSON parsing.");
        } else {
            Log.d("QuizManager", "Total questions loaded: " + this.questionList.size());
        }
    }

    // ✅ Save last completed level
    public void saveProgress(String level) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("last_level", level);
        editor.apply();
    }

    // ✅ Retrieve last completed level
    public String getLastLevel() {
        return sharedPreferences.getString("last_level", "easy");
    }

    // ✅ Get questions based on difficulty level
    public List<Question> getQuestionsByDifficulty(String difficulty) {
        List<Question> filteredQuestions = new ArrayList<>();

        if (difficulty == null) {
            return filteredQuestions; // 🔥 Prevents null crash
        }

        if (questionList != null) { // 🔥 Prevents NullPointerException
            for (Question q : questionList) {
                if (q != null && difficulty.equalsIgnoreCase(q.getDifficulty())) { // 🔥 Ensuring difficulty is NOT null
                    filteredQuestions.add(q);
                }
            }
        }

        return filteredQuestions;
    }

    // ✅ Retrieve the next level based on score
    public String getLevel() {
        int currentScore = sharedPreferences.getInt("score", 0);
        if (currentScore >= 7) {
            return "medium";
        } else if (currentScore >= 14) {
            return "hard";
        } else {
            return "easy";
        }
    }

    // ✅ Save the current score
    public void saveScore(int score) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("score", score);
        editor.apply();
    }

    // ✅ Retrieve current score
    public int getScore() {
        return sharedPreferences.getInt("score", 0);
    }
}
