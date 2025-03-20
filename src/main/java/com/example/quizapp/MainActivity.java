package com.example.quizapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private QuizManager quizManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        quizManager = new QuizManager(this);
        Button startQuizButton = findViewById(R.id.startQuizButton);

        startQuizButton.setOnClickListener(v -> {
            String lastLevel = quizManager.getLastLevel();
            Intent intent = new Intent(MainActivity.this, QuizActivity.class);
            intent.putExtra("level", lastLevel);
            startActivity(intent);
        });
    }
}
