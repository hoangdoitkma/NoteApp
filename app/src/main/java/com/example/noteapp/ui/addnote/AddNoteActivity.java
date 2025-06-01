
package com.example.noteapp.ui.addnote;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.example.noteapp.R;
import com.example.noteapp.data.NoteRepository;
import com.example.noteapp.model.Note;

public class AddNoteActivity extends AppCompatActivity {
    private EditText titleInput, contentInput;
    private NoteRepository repo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        titleInput = findViewById(R.id.editTitle);
        contentInput = findViewById(R.id.editContent);
        repo = new NoteRepository(this);

        findViewById(R.id.btnSave).setOnClickListener(v -> {
            String title = titleInput.getText().toString();
            String content = contentInput.getText().toString();
            Note note = new Note(title, content, System.currentTimeMillis());
            repo.addNote(note);
            finish();
        });
    }
}
