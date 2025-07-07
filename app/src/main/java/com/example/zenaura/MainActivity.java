package com.example.zenaura;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {


    private Button notesButton;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase Auth and Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        notesButton = findViewById(R.id.notes_button);



        notesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openNotesActivity();
            }
        });

        // Check if user is signed in
        if (mAuth.getCurrentUser() != null) {
            String userId = mAuth.getCurrentUser().getUid();

            // Check if the user has their profile info in Firestore
            checkUserProfile(userId);
        }

        // Profile button logic
        findViewById(R.id.profile_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openProfileActivity();
            }
        });
    }

    private void checkUserProfile(String userId) {
        db.collection("Users").document(userId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult() != null && task.getResult().exists()) {
                            // User info exists in Firestore, you can set the user info here
                            String name = task.getResult().getString("name");
                            String email = task.getResult().getString("email");

                            if (name != null && email != null) {
                                setTitle("Welcome " + name);  // Set the title with the user's name
                            }
                        } else {
                            // User info doesn't exist, prompt for details
                            promptForUserProfile(userId);
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "Error retrieving profile", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void promptForUserProfile(String userId) {
        // Here, you can show a dialog or direct to a new activity to ask for user details
        Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
        intent.putExtra("userId", userId);
        startActivity(intent);
    }


    private void openNotesActivity() {
        Intent intent = new Intent(MainActivity.this, NotesActivity.class);
        startActivity(intent);
    }

    private void openProfileActivity() {
        Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
        startActivity(intent);
    }
}
