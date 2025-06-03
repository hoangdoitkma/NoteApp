package com.example.noteapp.ui.addnote;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.noteapp.R;
import com.example.noteapp.data.NoteDao;
import com.example.noteapp.model.Note;
import com.example.noteapp.ui.notes.NotesFragment;

public class AddNoteActivity extends AppCompatActivity {

    private EditText edtTitle, edtContent;
    private Button btnSave;
    private NoteDao noteDao;
    private Note currentNote = null;  // Ghi chú đang chỉnh sửa (nếu có)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        edtTitle = findViewById(R.id.editTitle);
        edtContent = findViewById(R.id.editContent);
        btnSave = findViewById(R.id.btnSave);

        noteDao = new NoteDao(this);

        // Nhận noteId nếu có
        int noteId = getIntent().getIntExtra("noteId", -1);
        if (noteId != -1) {
            // Load ghi chú để sửa
            currentNote = noteDao.getNoteById(noteId);
            if (currentNote != null) {
                edtTitle.setText(currentNote.getTitle());
                edtContent.setText(currentNote.getContent());
            }
        }

        btnSave.setOnClickListener(v -> {
            String title = edtTitle.getText().toString().trim();
            String content = edtContent.getText().toString().trim();

            if (TextUtils.isEmpty(title)) {
                Toast.makeText(this, "Tiêu đề không được để trống", Toast.LENGTH_SHORT).show();
                return;
            }

            if (currentNote == null) {
                // Thêm mới ghi chú
                Note newNote = new Note();
                newNote.setTitle(title);
                newNote.setContent(content);
                noteDao.insertNote(newNote);
                Toast.makeText(this, "Đã thêm ghi chú", Toast.LENGTH_SHORT).show();

            } else {
                // Cập nhật ghi chú
                currentNote.setTitle(title);
                currentNote.setContent(content);
                noteDao.updateNote(currentNote);
                Toast.makeText(this, "Đã cập nhật ghi chú", Toast.LENGTH_SHORT).show();
            }

            finish(); // Quay về fragment danh sách ghi chú
        });
    }
}
