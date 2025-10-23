package com.example.fashionstore;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    private EditText userNameEditText, passwordEditText;
    private DatabaseReference databaseReference;

    // Admin credentials
    private static final String ADMIN_USERNAME = "achira";
    private static final String ADMIN_PASSWORD = "achira";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        userNameEditText = findViewById(R.id.uUsername);
        passwordEditText = findViewById(R.id.loginPassword);
        Button loginButton = findViewById(R.id.loginButton);
        TextView signUpTextView = findViewById(R.id.navigateSignUp);

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        loginButton.setOnClickListener(v -> handleLogin());
        signUpTextView.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void handleLogin() {
        String userName = userNameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (userName.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, getString(R.string.error_empty_credentials), Toast.LENGTH_SHORT).show();
            return;
        }

        // First check hardcoded admin credentials
        if (userName.equals(ADMIN_USERNAME) && password.equals(ADMIN_PASSWORD)) {
            loginAsAdmin(userName);
            return;
        }

        // If not admin, check regular user
        checkRegularUser(userName, password);
    }

    private void loginAsAdmin(String userName) {
        Toast.makeText(this, getString(R.string.welcome_admin), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, AdminDashboardActivity.class);
        intent.putExtra("username", userName);
        intent.putExtra("isAdmin", true);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void checkRegularUser(String userName, String password) {
        databaseReference.orderByChild("userName").equalTo(userName)
            .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.exists()) {
                        Toast.makeText(LoginActivity.this,
                            getString(R.string.error_user_not_found),
                            Toast.LENGTH_SHORT).show();
                        return;
                    }

                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        String dbPassword = userSnapshot.child("password").getValue(String.class);
                        String userId = userSnapshot.child("userId").getValue(String.class);

                        if (dbPassword != null && dbPassword.equals(password)) {
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.putExtra("username", userName);
                            intent.putExtra("user_id", userId);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                            return;
                        }
                    }
                    Toast.makeText(LoginActivity.this,
                        getString(R.string.error_invalid_password),
                        Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(LoginActivity.this,
                        getString(R.string.error_database, databaseError.getMessage()),
                        Toast.LENGTH_SHORT).show();
                }
            });
    }
}