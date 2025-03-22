package com.example.quizapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ResultActivity extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        // Initialize UI components
        TextView resultText = findViewById(R.id.resultText);
        TextView easyScoreText = findViewById(R.id.easyScoreText);
        TextView mediumScoreText = findViewById(R.id.mediumScoreText);
        TextView hardScoreText = findViewById(R.id.hardScoreText);
        TextView totalScoreText = findViewById(R.id.totalScoreText);
        Button restartButton = findViewById(R.id.restartButton);

        // Get scores from intent
        int easyScore = getIntent().getIntExtra("easyScore", 0);
        int mediumScore = getIntent().getIntExtra("mediumScore", 0);
        int hardScore = getIntent().getIntExtra("hardScore", 0);
        int totalScore = getIntent().getIntExtra("totalScore", 0);

        // Display scores
        easyScoreText.setText("Easy Level: " + easyScore + "/10");
        mediumScoreText.setText("Medium Level: " + mediumScore + "/10");
        hardScoreText.setText("Hard Level: " + hardScore + "/10");
        totalScoreText.setText("Total Score: " + totalScore + "/30");

        // Display final message based on total score
        if (totalScore >= 20) {
            resultText.setText("🌟 Excellent! You're a quiz master!");
            resultText.setTextColor(Color.YELLOW);
        } else if (totalScore >= 10) {
            resultText.setText("👍 Good job! Keep improving!");
            resultText.setTextColor(Color.YELLOW);
        } else {
            resultText.setText("💡 Keep practicing! You'll get better!");
            resultText.setTextColor(Color.YELLOW);
        }

        // Reset `quizCompleted` when restarting the quiz
        restartButton.setOnClickListener(v -> {
            SharedPreferences prefs = getSharedPreferences("QuizPrefs", MODE_PRIVATE);
            prefs.edit().putBoolean("quizCompleted", false).apply(); // Reset flag

            Intent intent = new Intent(ResultActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }

}
