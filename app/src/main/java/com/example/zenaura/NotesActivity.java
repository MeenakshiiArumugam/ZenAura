package com.example.zenaura;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class NotesActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private RecyclerView recyclerView;
    private NotesAdapter notesAdapter;
    private List<Note> notesList;
    private ImageButton addNoteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        recyclerView = findViewById(R.id.recyclerView);
        addNoteButton = findViewById(R.id.addNoteButton);

        notesList = new ArrayList<>();

        // Set up adapter with note click listeners
        notesAdapter = new NotesAdapter(this, notesList, new NotesAdapter.OnNoteActionListener() {
            @Override
            public void onEdit(Note note) {
                editNote(note);
            }

            @Override
            public void onDelete(Note note) {
                deleteNote(note);
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(notesAdapter);

        addNoteButton.setOnClickListener(v -> {
            Intent intent = new Intent(NotesActivity.this, CreateNoteActivity.class);
            startActivity(intent);
        });

        loadNotes(); // Initial load
    }

    private void loadNotes() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            CollectionReference notesRef = db.collection("Notes");
            notesRef.whereEqualTo("userId", user.getUid())
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            QuerySnapshot documentSnapshots = task.getResult();
                            notesList.clear();
                            if (documentSnapshots != null) {
                                for (DocumentSnapshot document : documentSnapshots) {
                                    Note note = document.toObject(Note.class);
                                    if (note != null) {
                                        note.setId(document.getId());
                                        notesList.add(note);
                                    }
                                }
                                notesAdapter.notifyDataSetChanged();
                            }
                        } else {
                            Toast.makeText(NotesActivity.this, "Error getting notes", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void editNote(Note note) {
        Intent intent = new Intent(NotesActivity.this, CreateNoteActivity.class);
        intent.putExtra("noteId", note.getId());
        intent.putExtra("title", note.getTitle());
        intent.putExtra("description", note.getDescription());
        startActivity(intent);
    }

    private void deleteNote(Note note) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            db.collection("Notes").document(note.getId())
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(NotesActivity.this, "Note deleted", Toast.LENGTH_SHORT).show();
                        loadNotes();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(NotesActivity.this, "Error deleting note", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadNotes();  // Refresh notes every time you return
    }
}
