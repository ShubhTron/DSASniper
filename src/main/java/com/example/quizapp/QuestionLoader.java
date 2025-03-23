package com.example.quizapp;

import android.content.Context;
import android.util.Log;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;

public class QuestionLoader {
    private static final String TAG = "QuestionLoader";
    private static QuestionLoader instance;
    private static Map<String, List<Question>> questionsMap;

    // ✅ Singleton Pattern
    public static QuestionLoader getInstance(Context context) {
        if (instance == null) {
            instance = new QuestionLoader(context);
        }
        return instance;
    }

    // ✅ Private Constructor to Load Questions Once
    private QuestionLoader(Context context) {
        if (questionsMap == null) {
            loadQuestionsFromJson(context);
        }
    }

    // ✅ Load Questions from JSON File
    private void loadQuestionsFromJson(Context context) {
        try {
            InputStream is = context.getAssets().open("questions.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String json = new String(buffer, StandardCharsets.UTF_8);

            Gson gson = new Gson();
            Type type = new TypeToken<Map<String, List<Question>>>() {}.getType();
            questionsMap = gson.fromJson(json, type);

            if (questionsMap == null) {
                Log.e(TAG, "Parsed questionsMap is NULL!");
                questionsMap = new HashMap<>();
            }

            // ✅ Debug Logs
            for (String difficulty : questionsMap.keySet()) {
                Log.d(TAG, "Loaded " + questionsMap.get(difficulty).size() + " questions for level: " + difficulty);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading questions.json", e);
            questionsMap = new HashMap<>();
        }
    }

    // ✅ Retrieve Random Questions for Given Difficulty
    public List<Question> getRandomQuestions(String difficulty, int numQuestions) {
        Log.d(TAG, "Fetching questions for level: " + difficulty);

        if (questionsMap == null) {
            Log.e(TAG, "questionsMap is NULL! Cannot fetch questions.");
            return new ArrayList<>();
        }

        List<Question> allQuestions = questionsMap.getOrDefault(difficulty, new ArrayList<>());
        Log.d(TAG, "Found " + allQuestions.size() + " questions for: " + difficulty);

        if (allQuestions.isEmpty()) {
            Log.e(TAG, "No questions available for difficulty: " + difficulty);
            return new ArrayList<>();
        }

        Collections.shuffle(allQuestions);
        return allQuestions.subList(0, Math.min(numQuestions, allQuestions.size()));
    }

    // ✅ Static Helper Method for Easy Access
    public static List<Question> loadQuestions(Context context, String difficulty, int numQuestions) {
        return getInstance(context).getRandomQuestions(difficulty, numQuestions);
    }
}
