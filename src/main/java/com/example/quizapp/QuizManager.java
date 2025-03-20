package com.example.quizapp;

import android.content.Context;
import android.content.SharedPreferences;

public class QuizManager {
    private SharedPreferences sharedPreferences;

    public QuizManager(Context context) {
        sharedPreferences = context.getSharedPreferences("QuizPrefs", Context.MODE_PRIVATE);
    }

    public void saveProgress(String level) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("last_level", level);
        editor.apply();
    }

    public String getLastLevel() {
        return sharedPreferences.getString("last_level", "easy");
    }
}
