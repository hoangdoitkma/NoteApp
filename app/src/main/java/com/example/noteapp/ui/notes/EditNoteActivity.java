package com.example.noteapp.ui.notes;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.noteapp.R;
import com.example.noteapp.data.NoteRepository;
import com.example.noteapp.model.Note;

public class EditNoteActivity extends AppCompatActivity {

    private EditText titleEditText, contentEditText;
    private NoteRepository repository;
    private Note note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);

        titleEditText = findViewById(R.id.editTitle);
        contentEditText = findViewById(R.id.editContent);
        Button saveButton = findViewById(R.id.btnSave);

        repository = new NoteRepository(this);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("note_id")) {
            int noteId = intent.getIntExtra("note_id", 0);
            note = repository.getNoteById(noteId);

            if (note == null) {
                Toast.makeText(this, "Không tìm thấy ghi chú!", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            if (note.isLocked()) {
                Toast.makeText(this, "Ghi chú đã bị khóa!", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            titleEditText.setText(note.getTitle());
            contentEditText.setText(note.getContent());
        } else {
            note = new Note();
            note.setTimestamp(System.currentTimeMillis());
        }

        saveButton.setOnClickListener(v -> {
            String title = titleEditText.getText().toString().trim();
            String content = contentEditText.getText().toString().trim();

            if (title.isEmpty() && content.isEmpty()) {
                Toast.makeText(this, "Không được để trống!", Toast.LENGTH_SHORT).show();
                return;
            }

            note.setTitle(title);
            note.setContent(content);
            note.setLastEdited(System.currentTimeMillis());

            if (note.getId() == 0) {
                repository.addNote(note);
            } else {
                repository.updateNote(note);
            }

            finish();
        });
    }
}
