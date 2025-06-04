package com.example.noteapp.ui.notes;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.noteapp.R;
import com.example.noteapp.model.Note;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NoteAdapter extends RecyclerView.Adapter<NoteViewHolder> {

    private List<Note> notes;

    public interface OnItemClickListener {
        void onItemClick(Note note);
        void onItemLongClick(Note note, int position);
    }

    private OnItemClickListener listener;

    public NoteAdapter(List<Note> notes) {
        this.notes = notes;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public List<Note> getNotes() {
        return notes;
    }

    public void setNotes(List<Note> notes) {
        this.notes = notes;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_note, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        Note note = notes.get(position);

        holder.titleTextView.setText(note.getTitle());
        holder.contentTextView.setText(note.getContent());

        // Định dạng ngày tháng cho lastEdited
        long lastEditedMillis = note.getLastEdited();
        String formattedDate = "";
        if (lastEditedMillis > 0) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            formattedDate = "Lần sửa cuối: " + sdf.format(new Date(lastEditedMillis));
        } else {
            formattedDate = "Chưa sửa";
        }
        holder.textViewLastEdited.setText(formattedDate);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(note);
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (listener != null) {
                listener.onItemLongClick(note, position);
                return true;
            }
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return notes != null ? notes.size() : 0;
    }
}
