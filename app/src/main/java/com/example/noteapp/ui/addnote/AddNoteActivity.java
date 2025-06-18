package com.example.noteapp.ui.addnote;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.noteapp.R;
import com.example.noteapp.model.Note;
import com.example.noteapp.ui.notes.NoteViewModel;
import com.google.android.material.appbar.MaterialToolbar;

import jp.wasabeef.richeditor.RichEditor;

public class AddNoteActivity extends AppCompatActivity {

    private EditText editTitle;
    private RichEditor richEditor;
    private NoteViewModel noteViewModel;
    private Note currentNote;
    private Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        // Ánh xạ view
        MaterialToolbar toolbar = findViewById(R.id.toolbarAddNote);
        editTitle = findViewById(R.id.editTitle);
        richEditor = findViewById(R.id.richEditor);
        btnSave = findViewById(R.id.btnSave);

        // Toolbar quay lại
        toolbar.setNavigationOnClickListener(v -> finish());

        // Thiết lập RichEditor
        richEditor.setEditorHeight(200);
        richEditor.setEditorFontSize(16);
        richEditor.setEditorFontColor(ContextCompat.getColor(this, R.color.black));
        richEditor.setPadding(10, 10, 10, 10);
        richEditor.setPlaceholder("Nhập nội dung...");

        // ViewModel
        noteViewModel = new ViewModelProvider(this).get(NoteViewModel.class);

        // Nếu có noteId -> đang chỉnh sửa
        int noteId = getIntent().getIntExtra("noteId", -1);
        if (noteId != -1) {
            noteViewModel.getNoteById(noteId).observe(this, new Observer<Note>() {
                @Override
                public void onChanged(Note note) {
                    if (note != null) {
                        currentNote = note;
                        editTitle.setText(note.getTitle());
                        richEditor.setHtml(note.getContent());
                    }
                }
            });
        }

        // Lưu
        btnSave.setOnClickListener(v -> {
            String title = editTitle.getText().toString().trim();
            String content = richEditor.getHtml();

            if (TextUtils.isEmpty(title)) {
                Toast.makeText(this, "Vui lòng nhập tiêu đề", Toast.LENGTH_SHORT).show();
                return;
            }

            long now = System.currentTimeMillis();

            if (currentNote == null) {
                Note newNote = new Note(title, content, now, false, false);
                noteViewModel.insert(newNote);
                Toast.makeText(this, "Đã thêm ghi chú", Toast.LENGTH_SHORT).show();
            } else {
                currentNote.setTitle(title);
                currentNote.setContent(content);
                currentNote.setLastEdited(now);
                noteViewModel.update(currentNote);
                Toast.makeText(this, "Đã cập nhật ghi chú", Toast.LENGTH_SHORT).show();
            }

            finish();
        });

        setupFormattingToolbar();
    }

    private void setupFormattingToolbar() {
        findViewById(R.id.btnBold).setOnClickListener(v -> richEditor.setBold());
        findViewById(R.id.btnItalic).setOnClickListener(v -> richEditor.setItalic());
        findViewById(R.id.btnUnderline).setOnClickListener(v -> richEditor.setUnderline());
        findViewById(R.id.btnStrikethrough).setOnClickListener(v -> richEditor.setStrikeThrough());
        findViewById(R.id.btnBullets).setOnClickListener(v -> richEditor.setBullets());
        findViewById(R.id.btnNumbers).setOnClickListener(v -> richEditor.setNumbers());
        findViewById(R.id.btnQuote).setOnClickListener(v -> richEditor.setBlockquote());
        findViewById(R.id.btnUndo).setOnClickListener(v -> richEditor.undo());
        findViewById(R.id.btnRedo).setOnClickListener(v -> richEditor.redo());
    }
}
