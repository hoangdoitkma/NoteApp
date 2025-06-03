package com.example.noteapp.ui.notes;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.noteapp.R;
import com.example.noteapp.model.Note;
import com.example.noteapp.util.BiometricHelper;

public class EditNoteActivity extends AppCompatActivity {

    private EditText titleEditText, contentEditText;
    private NoteViewModel noteViewModel;
    private Note note;
    private boolean isLockedNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);

        titleEditText = findViewById(R.id.editTitle);
        contentEditText = findViewById(R.id.editContent);
        Button saveButton = findViewById(R.id.btnSave);

        noteViewModel = new NoteViewModel(getApplication());

        Intent intent = getIntent();
        int noteId = intent.getIntExtra("note_id", 0);
        isLockedNote = intent.getBooleanExtra("is_locked_note", false);

        noteViewModel.getNoteById(noteId).observe(this, loadedNote -> {
            if (loadedNote == null) {
                Toast.makeText(this, "Không tìm thấy ghi chú!", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
            note = loadedNote;
            titleEditText.setText(note.getTitle());
            contentEditText.setText(note.getContent());

            if (isLockedNote) {
                titleEditText.setEnabled(false);
                contentEditText.setEnabled(false);
            }
        });

        // Long click để show dialog bỏ khóa nếu ghi chú đang khóa
        findViewById(R.id.rootLayout).setOnLongClickListener(v -> {
            if (isLockedNote) {
                showUnlockDialog();
                return true;
            }
            return false;
        });

        saveButton.setOnClickListener(v -> {
            if (isLockedNote) {
                Toast.makeText(this, "Ghi chú đang bị khóa, không thể chỉnh sửa", Toast.LENGTH_SHORT).show();
                return;
            }
            String title = titleEditText.getText().toString().trim();
            String content = contentEditText.getText().toString().trim();

            if (title.isEmpty() && content.isEmpty()) {
                Toast.makeText(this, "Không được để trống!", Toast.LENGTH_SHORT).show();
                return;
            }

            note.setTitle(title);
            note.setContent(content);
            note.setLastEdited(System.currentTimeMillis());

            noteViewModel.updateNote(note);
            finish();
        });
    }

    private void showUnlockDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Bỏ khóa ghi chú")
                .setMessage("Bạn có muốn bỏ khóa ghi chú này?")
                .setPositiveButton("Xác thực", (dialog, which) -> {
                    if (BiometricHelper.isBiometricAvailable(this)) {
                        BiometricHelper.showBiometricPrompt(this, success -> {
                            if (success) {
                                note.setLocked(false);
                                noteViewModel.updateNote(note);
                                Toast.makeText(this, "Đã bỏ khóa ghi chú", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(this, "Xác thực không thành công", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(this, "Thiết bị không hỗ trợ sinh trắc học", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}
