package com.example.noteapp.ui.notes;

import android.view.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.noteapp.R;
import com.example.noteapp.model.Note;

import java.util.ArrayList;
import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteViewHolder> {
    private final List<Note> notes = new ArrayList<>();
    private OnNoteClickListener listener;
    private OnNoteLongClickListener onNoteLongClickListener;

    public interface OnNoteClickListener {
        void onNoteClick(Note note);
    }
    public interface OnNoteLongClickListener {
        void onNoteLongClick(Note note);
    }

    public void setOnNoteLongClickListener(OnNoteLongClickListener listener) {
        this.onNoteLongClickListener = listener;
    }

    public void setOnNoteClickListener(OnNoteClickListener listener) {
        this.listener = listener;
    }

    public void setNotes(List<Note> newNotes) {
        notes.clear();
        notes.addAll(newNotes);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_note, parent, false);
        return new NoteViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int pos) {
        Note note = notes.get(pos);
        if (note.isLocked()) {
            holder.titleTextView.setText("Ghi chú bị khóa");
            holder.contentTextView.setText("");
        } else {
            holder.titleTextView.setText(note.getTitle());
            holder.contentTextView.setText(note.getContent());
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onNoteClick(note);
        });
        holder.itemView.setOnLongClickListener(v -> {
            if (onNoteLongClickListener != null) onNoteLongClickListener.onNoteLongClick(note);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }
}
