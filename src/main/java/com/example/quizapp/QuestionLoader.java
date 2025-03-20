package com.example.quizapp;

import android.content.Context;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;

public class QuestionLoader {
    private Map<String, List<Question>> questionsMap;

    public QuestionLoader(Context context) {
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
        } catch (Exception e) {
            e.printStackTrace();
            questionsMap = new HashMap<>();
        }
    }

    public List<Question> getRandomQuestions(String difficulty, int numQuestions) {
        List<Question> allQuestions = questionsMap.getOrDefault(difficulty, new ArrayList<>());
        Collections.shuffle(allQuestions);
        return allQuestions.subList(0, Math.min(numQuestions, allQuestions.size()));
    }
}
