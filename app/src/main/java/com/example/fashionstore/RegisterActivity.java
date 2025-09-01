package com.example.fashionstore;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private EditText userNameEditText, passwordEditText, emailEditText;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register); // Make sure this matches your XML file name

        // Initialize EditText fields
        userNameEditText = findViewById(R.id.registerUserName);
        passwordEditText = findViewById(R.id.registerPassword);
        emailEditText = findViewById(R.id.registerEmail);

        Button registerButton = findViewById(R.id.registerButton);
        TextView navigateSignUp = findViewById(R.id.navigateSignUp);

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        registerButton.setOnClickListener(v -> {
            String userName = userNameEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();

            if (userName.isEmpty() || password.isEmpty() || email.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Generate a unique user ID
            String userId = databaseReference.push().getKey();

            // Create a user object
            User user = new User(userId, userName, password, email);

            // Store user data in Firebase under the generated ID
            databaseReference.child(userId).setValue(user)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Registered successfully!", Toast.LENGTH_SHORT).show();
                        // Navigate to LoginActivity
                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Registration failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
        });

        navigateSignUp.setOnClickListener(v -> {
            // Navigate to LoginActivity
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });
    }

    // User class for Firebase
    public static class User {
        public String userId;
        public String userName;
        public String password;
        public String email;

        // Default constructor required for Firebase
        public User() {
        }

        public User(String userId, String userName, String password, String email) {
            this.userId = userId;
            this.userName = userName;
            this.password = password;
            this.email = email;
        }
    }
}