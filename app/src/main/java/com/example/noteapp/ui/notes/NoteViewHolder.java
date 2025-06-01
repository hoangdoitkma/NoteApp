package com.example.noteapp.ui.notes;

import android.view.View;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.noteapp.R;

public class NoteViewHolder extends RecyclerView.ViewHolder {
    TextView titleTextView;
    TextView contentTextView;

    public NoteViewHolder(View itemView) {
        super(itemView);
        titleTextView = itemView.findViewById(R.id.noteTitle);
        contentTextView = itemView.findViewById(R.id.noteContent);
    }
}
