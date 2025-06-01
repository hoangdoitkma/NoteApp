package com.example.noteapp.data;

import android.content.Context;
import com.example.noteapp.model.Note;

import java.util.List;

public class NoteRepository {
    private final DatabaseHelper db;

    public NoteRepository(Context context) {
        db = new DatabaseHelper(context);
    }

    public void addNote(Note note) {
        db.insertNote(note);
    }

    public List<Note> getAllNotes() {
        return db.getAllNotes();
    }
    public void deleteNote(Note note) {
        db.deleteNote(note.getId());
    }

    public void updateNote(Note note) {
        db.updateNote(note);
    }
    // ✅ Lấy ghi chú theo ID
    public Note getNoteById(int id) {
        return db.getNoteById(id);
    }

    // ✅ Cập nhật ghi chú
}
