package com.example.noteapp.ui.notes;

import android.view.View;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.noteapp.R;

public class NoteViewHolder extends RecyclerView.ViewHolder {
    TextView titleTextView;
    TextView contentTextView;
    TextView textViewLastEdited;
    public NoteViewHolder(View itemView) {
        super(itemView);
        titleTextView = itemView.findViewById(R.id.noteTitle);
        contentTextView = itemView.findViewById(R.id.noteContent);
        textViewLastEdited = itemView.findViewById(R.id.textViewLastEdited);
    }

    public TextView getTitleTextView() {
        return titleTextView;
    }

    public void setTitleTextView(TextView titleTextView) {
        this.titleTextView = titleTextView;
    }

    public TextView getContentTextView() {
        return contentTextView;
    }

    public void setContentTextView(TextView contentTextView) {
        this.contentTextView = contentTextView;
    }

    public TextView getTextViewLastEdited() {
        return textViewLastEdited;
    }

    public void setTextViewLastEdited(TextView textViewLastEdited) {
        this.textViewLastEdited = textViewLastEdited;
    }
}
