package com.example.zenaura;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteViewHolder> {

    private Context context;
    private List<Note> notes;
    private OnNoteActionListener listener;

    public interface OnNoteActionListener {
        void onEdit(Note note);
        void onDelete(Note note);
    }

    public NotesAdapter(Context context, List<Note> notes, OnNoteActionListener listener) {
        this.context = context;
        this.notes = notes;
        this.listener = listener;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.note_item, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        Note note = notes.get(position);
        holder.bind(note);

        holder.editButton.setOnClickListener(v -> {
            if (listener != null) listener.onEdit(note);
        });

        holder.deleteButton.setOnClickListener(v -> {
            if (listener != null) listener.onDelete(note);
        });
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public class NoteViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView titleTextView, descriptionTextView, timestampTextView;
        ImageButton editButton, deleteButton;
        View cardInnerLayout, colorStripView;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
            timestampTextView = itemView.findViewById(R.id.timestampTextView);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            cardInnerLayout = itemView.findViewById(R.id.cardInnerLayout);
            colorStripView = itemView.findViewById(R.id.colorStripView);
        }

        public void bind(Note note) {
            titleTextView.setText(note.getTitle());
            descriptionTextView.setText(note.getDescription());

            if (note.getTimestamp() != null) {
                timestampTextView.setText(new SimpleDateFormat("MMM dd, hh:mm a").format(note.getTimestamp()));
            } else {
                timestampTextView.setText("");
            }

            String colorString = "#FFFFFF"; // default white
            if (note.getLabel() != null && note.getLabel().getColor() != null) {
                colorString = note.getLabel().getColor();
            }

            try {
                int color = Color.parseColor(colorString);
                cardInnerLayout.setBackgroundColor(color);
                colorStripView.setBackgroundColor(color);
            } catch (IllegalArgumentException e) {
                cardInnerLayout.setBackgroundColor(Color.WHITE);
                colorStripView.setBackgroundColor(Color.BLACK);
            }
        }
    }
}
