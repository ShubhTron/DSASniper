package com.example.quizapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

public class QuizActivity extends AppCompatActivity {

    private TextView questionText, levelText, scoreText, feedbackText;
    private RadioGroup optionsGroup;
    private Button nextButton;

    private List<Question> questionList;
    private int currentQuestionIndex = 0;
    private int score = 0;
    private int totalScore = 0;
    private int easyScore = 0, mediumScore = 0, hardScore = 0;
    private String currentLevel = "Easy";

    private static final int PASSING_SCORE = 7;  // Score required to advance levels

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        // Initialize UI components
        questionText = findViewById(R.id.questionText);
        levelText = findViewById(R.id.levelText);
        scoreText = findViewById(R.id.scoreText);
        feedbackText = findViewById(R.id.feedbackText);
        optionsGroup = findViewById(R.id.optionsGroup);
        nextButton = findViewById(R.id.nextButton);

        // Get difficulty level from Intent
        String difficulty = getIntent().getStringExtra("Level");
        if (difficulty == null) {
            difficulty = "easy";  // Default to "easy"
        }

        // Load questions for the selected level
        loadQuestions(difficulty);

        nextButton.setOnClickListener(v -> checkAnswer());
    }

    private void loadQuestions(String difficulty) {
        currentLevel = capitalizeFirstLetter(difficulty);
        questionList = QuestionLoader.loadQuestions(this, difficulty, 10);

        if (questionList.isEmpty()) {
            Log.e("QuizActivity", "No questions available! Check JSON file.");
            questionText.setText("No questions available.");
            return;
        }

        currentQuestionIndex = 0;
        score = 0;
        levelText.setText("Level: " + currentLevel);
        scoreText.setText("Score: " + score);
        loadNextQuestion();
    }

    private void loadNextQuestion() {
        if (currentQuestionIndex < questionList.size()) {
            Question currentQuestion = questionList.get(currentQuestionIndex);
            questionText.setText(currentQuestion.getQuestion());
            optionsGroup.removeAllViews();

            for (String option : currentQuestion.getOptions()) {
                RadioButton radioButton = new RadioButton(this);
                radioButton.setText(option);
                optionsGroup.addView(radioButton);
            }

            optionsGroup.clearCheck(); // ✅ Clears previous selection
        } else {
            checkLevelProgress();  // ✅ Only run when all questions in a level are completed
        }
    }

    private void checkAnswer() {
        int selectedId = optionsGroup.getCheckedRadioButtonId();
        if (selectedId == -1) {
            feedbackText.setText("Please select an answer!"); // ✅ Prevents skipping without selecting
            feedbackText.setTextColor(Color.RED);
            return;
        }

        RadioButton selectedRadioButton = findViewById(selectedId);
        String selectedAnswer = selectedRadioButton.getText().toString();

        if (currentQuestionIndex < questionList.size()) {
            Question currentQuestion = questionList.get(currentQuestionIndex);
            if (selectedAnswer.equals(currentQuestion.getCorrectAnswer())) {
                feedbackText.setText("✅ Correct!");
                feedbackText.setTextColor(Color.GREEN);
                score++;

                // ✅ Update score in UI
                scoreText.setText("Score: " + score);
            } else {
                feedbackText.setText("❌ Wrong! Correct answer: " + currentQuestion.getCorrectAnswer());
                feedbackText.setTextColor(Color.RED);
            }

            currentQuestionIndex++; // ✅ Move to next question properly

            // ✅ Delay moving to next question so feedback is visible
            nextButton.setEnabled(false);
            nextButton.postDelayed(() -> {
                feedbackText.setText(""); // ✅ Clears feedback after delay
                nextButton.setEnabled(true);

                if (currentQuestionIndex < questionList.size()) {
                    loadNextQuestion();
                } else {
                    checkLevelProgress();
                }
            }, 500); // ✅ 0.5 second delay
        }
    }


    private void checkLevelProgress() {
        totalScore += score;  // ✅ Add to total score

        if (currentLevel.equals("Easy")) {
            easyScore = score;
            if (score >= PASSING_SCORE) {
                Log.d("QuizActivity", "Moving to Medium level...");
                currentQuestionIndex = 0;  // ✅ Reset question index
                loadQuestions("medium");
                return;
            }
        } else if (currentLevel.equals("Medium")) {
            mediumScore = score;
            if (score >= PASSING_SCORE) {
                Log.d("QuizActivity", "Moving to Hard level...");
                currentQuestionIndex = 0;  // ✅ Reset question index
                loadQuestions("hard");
                return;
            }
        } else if (currentLevel.equals("Hard")) {
            hardScore = score;
        }

        Log.d("QuizActivity", "Quiz Completed! Moving to ResultActivity...");
        endQuiz();  // ✅ End the quiz only after completing all levels
    }

    private void endQuiz() {
        // ✅ Store quiz completion flag
        SharedPreferences prefs = getSharedPreferences("QuizPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("quizCompleted", true);
        editor.apply();

        // ✅ Start ResultActivity and pass scores
        Intent intent = new Intent(this, ResultActivity.class);
        intent.putExtra("easyScore", easyScore);
        intent.putExtra("mediumScore", mediumScore);
        intent.putExtra("hardScore", hardScore);
        intent.putExtra("totalScore", totalScore);
        startActivity(intent);
        finish();
    }

    private String capitalizeFirstLetter(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
