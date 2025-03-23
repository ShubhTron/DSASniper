package com.example.quizapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ResultActivity extends AppCompatActivity {
    private String userName = "Player"; // Default name
    private int totalScore;
    private ImageView certificateView;
    private FirebaseFirestore db;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        // Firebase instances
        db = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        // UI Components
        TextView resultText = findViewById(R.id.resultText);
        TextView easyScoreText = findViewById(R.id.easyScoreText);
        TextView mediumScoreText = findViewById(R.id.mediumScoreText);
        TextView hardScoreText = findViewById(R.id.hardScoreText);
        TextView totalScoreText = findViewById(R.id.totalScoreText);
        Button restartButton = findViewById(R.id.restartButton);
        Button downloadCertificateButton = findViewById(R.id.downloadCertificateButton);
        certificateView = findViewById(R.id.certificateView);

        // Get scores from Intent
        int easyScore = getIntent().getIntExtra("easyScore", 0);
        int mediumScore = getIntent().getIntExtra("mediumScore", 0);
        int hardScore = getIntent().getIntExtra("hardScore", 0);
        totalScore = getIntent().getIntExtra("totalScore", 0);
        userName = getIntent().getStringExtra("userName");

        // If username is missing, fetch from Firestore
        if (userName == null || userName.isEmpty()) {
            fetchUserNameFromFirestore();
        } else {
            generateCertificateAndDisplay();
        }

        // Set scores in UI
        easyScoreText.setText("Easy Level: " + easyScore + "/10");
        mediumScoreText.setText("Medium Level: " + mediumScore + "/10");
        hardScoreText.setText("Hard Level: " + hardScore + "/10");
        totalScoreText.setText("Total Score: " + totalScore + "/30");

        // Set Result Message
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

        // Download Certificate
        downloadCertificateButton.setOnClickListener(v -> {
            Bitmap certificateBitmap = ((BitmapDrawable) certificateView.getDrawable()).getBitmap();
            saveCertificate(certificateBitmap);
        });

        // Restart Quiz
        restartButton.setOnClickListener(v -> {
            SharedPreferences prefs = getSharedPreferences("QuizPrefs", MODE_PRIVATE);
            prefs.edit().putBoolean("quizCompleted", false).apply();

            Intent intent = new Intent(ResultActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void fetchUserNameFromFirestore() {
        if (firebaseUser != null) {
            db.collection("Users").document(firebaseUser.getUid()).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            userName = documentSnapshot.getString("name");
                            if (userName == null || userName.trim().isEmpty()) {
                                userName = "Player";
                            }
                        }
                        generateCertificateAndDisplay();
                    })
                    .addOnFailureListener(e -> {
                        userName = "Player";
                        generateCertificateAndDisplay();
                    });
        } else {
            userName = "Player";
            generateCertificateAndDisplay();
        }
    }

    private void generateCertificateAndDisplay() {
        Bitmap certificate = generateCertificate(userName, totalScore);
        certificateView.setImageBitmap(certificate);
    }

    private Bitmap generateCertificate(String name, int score) {
        int width = 800, height = 600;
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        // Background Color
        canvas.drawColor(Color.WHITE);

        // Paint for Text
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(50);
        paint.setTypeface(Typeface.DEFAULT_BOLD);

        // Draw Certificate Text
        canvas.drawText("Certificate of Completion", 180, 150, paint);
        paint.setTextSize(40);
        canvas.drawText("Awarded to:", 300, 250, paint);
        paint.setTextSize(60);
        paint.setTypeface(Typeface.SERIF);
        canvas.drawText(name, 280, 330, paint);
        paint.setTypeface(Typeface.DEFAULT_BOLD);
        paint.setTextSize(40);
        canvas.drawText("For scoring " + score + "/30 in the DSASniper", 150, 400, paint);

        return bitmap;
    }

    private void saveCertificate(Bitmap certificate) {
        File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "QuizApp");
        if (!directory.exists()) {
            directory.mkdirs();
        }

        File file = new File(directory, "Certificate_" + userName + ".png");
        try (FileOutputStream fos = new FileOutputStream(file)) {
            certificate.compress(Bitmap.CompressFormat.PNG, 100, fos);
            Toast.makeText(this, "Certificate saved in Downloads!", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Toast.makeText(this, "Error saving certificate: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
