package com.example.quizapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";  // ✅ Debugging tag

    private QuizManager quizManager;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;
    private TextView userGreetingText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        FirebaseUser user = firebaseAuth.getCurrentUser();

        userGreetingText = findViewById(R.id.userGreetingText);
        TextView welcomeText = findViewById(R.id.welcomeText);
        Button loginButton = findViewById(R.id.loginButton);
        Button logoutButton = findViewById(R.id.logoutButton);
        Button startQuizButton = findViewById(R.id.startQuizButton);

        if (user != null) {
            userGreetingText.setText("Hi, Loading...");  // ✅ Show loading state
            fetchUserName(user.getUid());
            userGreetingText.setVisibility(View.VISIBLE);
            welcomeText.setVisibility(View.GONE);
            loginButton.setVisibility(View.GONE);
            logoutButton.setVisibility(View.VISIBLE);
        } else {
            userGreetingText.setVisibility(View.GONE);
            welcomeText.setVisibility(View.VISIBLE);
            loginButton.setVisibility(View.VISIBLE);
            logoutButton.setVisibility(View.GONE);
        }

        // ✅ Initialize QuizManager
        quizManager = new QuizManager(this, null);
        QuestionLoader questionLoader = QuestionLoader.getInstance(this);
        List<Question> questions = questionLoader.getRandomQuestions("easy", 10);
        quizManager = new QuizManager(this, questions);

        startQuizButton.setOnClickListener(view -> {
            String lastLevel = quizManager.getLevel();
            Intent intent = new Intent(MainActivity.this, QuizActivity.class);
            intent.putExtra("Level", lastLevel);
            startActivity(intent);
        });

        loginButton.setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        });

        logoutButton.setOnClickListener(view -> {
            firebaseAuth.signOut();
            userGreetingText.setVisibility(View.GONE);
            welcomeText.setVisibility(View.VISIBLE);
            loginButton.setVisibility(View.VISIBLE);
            logoutButton.setVisibility(View.GONE);
        });
    }

    private void fetchUserName(String userId) {
        db.collection("Users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists() && documentSnapshot.contains("name")) {
                        String name = documentSnapshot.getString("name");
                        if (name != null && !name.trim().isEmpty()) {
                            userGreetingText.setText("Hi, " + name + "!");
                            Log.d(TAG, "User name fetched: " + name);  // ✅ Debugging log
                        } else {
                            userGreetingText.setText("Hi, User!");
                        }
                    } else {
                        userGreetingText.setText("Hi, User!");
                    }
                })
                .addOnFailureListener(e -> {
                    userGreetingText.setText("Hi, User!");
                    Log.e(TAG, "Error fetching user name: ", e);  // ✅ Log error for debugging
                });
    }
}
