package com.example.quizapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class QuizActivity extends AppCompatActivity {
    private TextView questionText, scoreText, feedbackText;
    private RadioGroup optionsGroup;
    private Button nextButton;

    private List<Question> questions;
    private int currentIndex = 0;
    private int score = 0;
    private boolean isAnswered = false; // Tracks if user answered the question

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        // Bind UI elements
        questionText = findViewById(R.id.questionText);
        scoreText = findViewById(R.id.scoreText);
        feedbackText = findViewById(R.id.feedbackText);
        optionsGroup = findViewById(R.id.optionsGroup);
        nextButton = findViewById(R.id.nextButton);

        // Get the level from intent
        String level = getIntent().getStringExtra("LEVEL");
        if (level == null) level = "easy"; // Default level

        // Load questions
        questions = new QuestionLoader(this).getRandomQuestions(level, 10);
        Log.d("QuizActivity", "Loaded Questions: " + (questions != null ? questions.size() : "null"));

        if (questions == null || questions.isEmpty()) {
            Toast.makeText(this, "No questions available!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Load the first question
        loadQuestion();

        // Disable button until an answer is selected
        nextButton.setEnabled(false);

        // Enable button when an answer is selected
        optionsGroup.setOnCheckedChangeListener((group, checkedId) -> nextButton.setEnabled(true));

        // Handle submit/next button clicks
        nextButton.setOnClickListener(v -> {
            if (!isAnswered) {
                checkAnswer(); // First click submits the answer
            } else {
                nextQuestion(); // Second click moves to the next question
            }
        });
    }

    private void loadQuestion() {
        isAnswered = false; // Reset answer flag
        feedbackText.setText(""); // Clear previous feedback
        optionsGroup.clearCheck(); // Clear previous selection
        optionsGroup.removeAllViews(); // Remove old options
        nextButton.setText("Submit"); // Reset button text
        nextButton.setEnabled(false); // Disable button until selection

        if (currentIndex < questions.size()) {
            Question q = questions.get(currentIndex);
            questionText.setText(q.getQuestion());

            for (String option : q.getOptions()) {
                RadioButton radioButton = new RadioButton(this);
                radioButton.setText(option);
                radioButton.setTextSize(16);
                radioButton.setPadding(10, 10, 10, 10);
                optionsGroup.addView(radioButton);
            }
        }
    }

    private void checkAnswer() {
        int selectedId = optionsGroup.getCheckedRadioButtonId();
        if (selectedId != -1) {
            isAnswered = true; // Mark as answered
            RadioButton selectedButton = findViewById(selectedId);
            String selectedAnswer = selectedButton.getText().toString();
            String correctAnswer = questions.get(currentIndex).getAnswer();

            if (selectedAnswer.equals(correctAnswer)) {
                score++;
                feedbackText.setText("✅ Correct!");
                feedbackText.setTextColor(Color.GREEN);
            } else {
                feedbackText.setText("❌ Wrong! Correct: " + correctAnswer);
                feedbackText.setTextColor(Color.RED);
            }

            scoreText.setText("Score: " + score); // Update score
            nextButton.setText("Next Question"); // Change button text
            nextButton.setEnabled(true); // Ensure button is enabled for next click
        } else {
            Toast.makeText(this, "Please select an option!", Toast.LENGTH_SHORT).show();
        }
    }

    private void nextQuestion() {
        currentIndex++;
        if (currentIndex < questions.size()) {
            loadQuestion();
        } else {
            moveToNextLevel();
        }
    }

    private void moveToNextLevel() {
        Toast.makeText(this, "Quiz Completed! Final Score: " + score, Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("FINAL_SCORE", score);
        startActivity(intent);
        finish();
    }
}
