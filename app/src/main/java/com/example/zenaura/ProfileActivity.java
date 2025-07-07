package com.example.zenaura;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private EditText nameEditText;
    private EditText emailEditText;
    private Button saveButton;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Get UI elements
        nameEditText = findViewById(R.id.name_edit_text);
        emailEditText = findViewById(R.id.email_edit_text);
        saveButton = findViewById(R.id.save_button);

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // Pre-fill name and email
            nameEditText.setText(currentUser.getDisplayName());
            emailEditText.setText(currentUser.getEmail());

            // Optional: fetch from Firestore and update fields
            fetchUserProfile(currentUser.getUid());
        } else {
            Toast.makeText(this, "User not signed in.", Toast.LENGTH_SHORT).show();
            finish();
        }

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentUser != null) {
                    saveUserProfile(currentUser.getUid());
                } else {
                    Toast.makeText(ProfileActivity.this, "Cannot save. User not signed in.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void fetchUserProfile(String userId) {
        db.collection("Users").document(userId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && task.getResult().exists()) {
                        nameEditText.setText(task.getResult().getString("name"));
                        emailEditText.setText(task.getResult().getString("mail"));
                    }
                });
    }

    private void saveUserProfile(String userId) {
        String name = nameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> userProfile = new HashMap<>();
        userProfile.put("name", name);
        userProfile.put("email", email);

        db.collection("Users").document(userId)
                .set(userProfile)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(ProfileActivity.this, "Profile saved successfully", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ProfileActivity.this, "Error saving profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
