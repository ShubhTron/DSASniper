package com.example.quizapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {
    private EditText emailInput, passwordInput, nameInput;
    private Button signupButton;
    private TextView loginRedirect;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        emailInput = findViewById(R.id.signupEmailInput);
        passwordInput = findViewById(R.id.signupPasswordInput);
        nameInput = findViewById(R.id.nameInput);
        signupButton = findViewById(R.id.signupButton);
        loginRedirect = findViewById(R.id.loginRedirect);  // 🔹 Initialize login redirect TextView

        signupButton.setOnClickListener(view -> registerUser());

        // 🔹 Redirect to login page when clicking "Already have an account? Login"
        loginRedirect.setOnClickListener(view -> {
            Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();  // Close SignupActivity
        });
    }

    private void registerUser() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String name = nameInput.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            nameInput.setError("Name is required!");
            return;
        }
        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInput.setError("Enter a valid email!");
            return;
        }
        if (TextUtils.isEmpty(password) || password.length() < 6) {
            passwordInput.setError("Password must be at least 6 characters!");
            return;
        }

        Toast.makeText(SignupActivity.this, "Creating your account...", Toast.LENGTH_SHORT).show();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            Toast.makeText(SignupActivity.this, "Signup Successful!", Toast.LENGTH_SHORT).show();

                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(name)
                                    .build();
                            user.updateProfile(profileUpdates)
                                    .addOnCompleteListener(task1 -> saveUserToDatabase(user.getUid(), name, email));

                            startActivity(new Intent(SignupActivity.this, MainActivity.class));
                            finish();
                        }
                    } else {
                        Toast.makeText(SignupActivity.this, "Signup Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveUserToDatabase(String userId, String name, String email) {
        Map<String, Object> user = new HashMap<>();
        user.put("name", name);
        user.put("email", email);
        user.put("score", 0);

        db.collection("Users").document(userId)
                .set(user)
                .addOnSuccessListener(aVoid -> new AlertDialog.Builder(SignupActivity.this)
                        .setTitle("Signup Successful!")
                        .setMessage("Welcome, " + name + "! Your account has been created successfully.")
                        .setPositiveButton("OK", (dialog, which) -> {
                            startActivity(new Intent(SignupActivity.this, MainActivity.class));
                            finish();
                        })
                        .setCancelable(false)
                        .show())
                .addOnFailureListener(e -> Toast.makeText(SignupActivity.this, "Database Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
