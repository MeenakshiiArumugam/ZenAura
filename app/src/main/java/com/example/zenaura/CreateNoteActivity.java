package com.example.zenaura;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentReference;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CreateNoteActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private EditText titleEditText, descriptionEditText, customLabelEditText;
    private View selectedColorView;
    private Button saveButton;

    private String noteId;
    private int selectedColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_note);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        titleEditText = findViewById(R.id.titleEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        customLabelEditText = findViewById(R.id.customLabelEditText);
        selectedColorView = findViewById(R.id.selectedColorView);
        saveButton = findViewById(R.id.saveButton);

        Intent intent = getIntent();
        if (intent.hasExtra("noteId")) {
            noteId = intent.getStringExtra("noteId");
            titleEditText.setText(intent.getStringExtra("title"));
            descriptionEditText.setText(intent.getStringExtra("description"));
            saveButton.setText(R.string.update_note);
        } else {
            noteId = null;
            saveButton.setText(R.string.save_note);
        }

        // Initial random pastel color
        selectedColor = getRandomPastelColor();
        selectedColorView.setBackgroundColor(selectedColor);

        // Tap to cycle color
        selectedColorView.setOnClickListener(v -> {
            selectedColor = getRandomPastelColor();
            selectedColorView.setBackgroundColor(selectedColor);
        });

        saveButton.setOnClickListener(v -> {
            if (noteId == null) {
                saveNewNote();
            } else {
                updateNote();
            }
        });
    }

    private void saveNewNote() {
        String title = titleEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();

        if (title.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, getString(R.string.error_empty_fields), Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            Toast.makeText(this, getString(R.string.error_login_required), Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> labelMap = getSelectedLabel();

        Map<String, Object> noteMap = new HashMap<>();
        noteMap.put("title", title);
        noteMap.put("description", description);
        noteMap.put("userId", user.getUid());
        noteMap.put("timestamp", new Date());
        noteMap.put("label", labelMap);

        db.collection("Notes")
                .add(noteMap)
                .addOnSuccessListener(docRef -> {
                    Toast.makeText(this, getString(R.string.note_saved), Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(this, getString(R.string.error_saving_note), Toast.LENGTH_SHORT).show());
    }

    private void updateNote() {
        String title = titleEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();

        if (title.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, getString(R.string.error_empty_fields), Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            Toast.makeText(this, getString(R.string.error_login_required), Toast.LENGTH_SHORT).show();
            return;
        }

        DocumentReference noteRef = db.collection("Notes").document(noteId);

        Map<String, Object> labelMap = getSelectedLabel();

        Map<String, Object> updates = new HashMap<>();
        updates.put("title", title);
        updates.put("description", description);
        updates.put("timestamp", new Date());
        updates.put("label", labelMap);

        noteRef.update(updates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, getString(R.string.note_updated), Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(this, getString(R.string.error_updating_note), Toast.LENGTH_SHORT).show());
    }

    private Map<String, Object> getSelectedLabel() {
        Map<String, Object> label = new HashMap<>();
        String customLabel = customLabelEditText.getText().toString().trim();

        if (!customLabel.isEmpty()) {
            label.put("name", customLabel);
            label.put("color", String.format("#%06X", (0xFFFFFF & selectedColor)));
        }

        return label;
    }

    private int getRandomPastelColor() {
        float[] hsv = new float[3];
        hsv[0] = (float) (Math.random() * 360); // hue
        hsv[1] = 0.4f; // pastel saturation
        hsv[2] = 0.9f; // pastel brightness
        return Color.HSVToColor(hsv);
    }
}
