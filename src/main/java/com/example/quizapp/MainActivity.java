package com.example.quizapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private QuizManager quizManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ✅ Check if the quiz was completed
        quizManager = new QuizManager(this, null);
        boolean quizCompleted = getSharedPreferences("QuizPrefs", MODE_PRIVATE)
                .getBoolean("quizCompleted", false);

        if (quizCompleted) {
            // ✅ If quiz was completed, go to ResultActivity instead
            Intent intent = new Intent(MainActivity.this, ResultActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        // ✅ Load questions from JSON file
        QuestionLoader questionLoader = QuestionLoader.getInstance(this);
        List<Question> questions = questionLoader.getRandomQuestions("easy", 10); // Load easy-level questions

        // ✅ Pass the question list to QuizManager
        quizManager = new QuizManager(this, questions);

        Button startQuizButton = findViewById(R.id.startQuizButton);
        startQuizButton.setOnClickListener(view -> {
            String lastLevel = quizManager.getLevel();
            Intent intent = new Intent(MainActivity.this, QuizActivity.class);
            intent.putExtra("Level", lastLevel);
            startActivity(intent);
        });
    }
}
