package com.example.noteapp.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.noteapp.model.Note;

import java.util.ArrayList;
import java.util.List;

public class NoteDao {

    private final NoteDatabaseHelper dbHelper;

    public NoteDao(Context context) {
        dbHelper = NoteDatabaseHelper.getInstance(context);
    }

    public long insertNote(Note note) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("title", note.getTitle());
        cv.put("content", note.getContent());
        cv.put("timestamp", note.getTimestamp());
        cv.put("locked", note.isLocked() ? 1 : 0);
        cv.put("lastEdited", note.getLastEdited());
        return db.insert("notes", null, cv);
    }

    public List<Note> getAllNotes() {
        List<Note> notes = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("notes", null, null, null, null, null, "lastEdited DESC");

        if (cursor.moveToFirst()) {
            do {
                Note n = cursorToNote(cursor);
                notes.add(n);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return notes;
    }

    public List<Note> getNotesWhereLocked(boolean locked) {
        List<Note> notes = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("notes", null, "locked = ?", new String[]{locked ? "1" : "0"}, null, null, "lastEdited DESC");

        if (cursor.moveToFirst()) {
            do {
                notes.add(cursorToNote(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return notes;
    }

    public Note getNoteById(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("notes", null, "id = ?", new String[]{String.valueOf(id)}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            Note note = cursorToNote(cursor);
            cursor.close();
            return note;
        }
        return null;
    }

    public int updateNote(Note note) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("title", note.getTitle());
        cv.put("content", note.getContent());
        cv.put("timestamp", note.getTimestamp());
        cv.put("locked", note.isLocked() ? 1 : 0);
        cv.put("lastEdited", note.getLastEdited());
        cv.put("pinned", note.isPinned() ? 1 : 0);
        return db.update("notes", cv, "id = ?", new String[]{String.valueOf(note.getId())});
    }

    public void deleteNote(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("notes", "id = ?", new String[]{String.valueOf(id)});
    }

    private Note cursorToNote(Cursor cursor) {
        Note note = new Note();
        note.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
        note.setTitle(cursor.getString(cursor.getColumnIndexOrThrow("title")));
        note.setContent(cursor.getString(cursor.getColumnIndexOrThrow("content")));
        note.setTimestamp(cursor.getLong(cursor.getColumnIndexOrThrow("timestamp")));
        note.setLocked(cursor.getInt(cursor.getColumnIndexOrThrow("locked")) == 1);
        note.setLastEdited(cursor.getLong(cursor.getColumnIndexOrThrow("lastEdited")));
        note.setPinned(cursor.getInt(cursor.getColumnIndexOrThrow("pinned")) == 1);  // thêm dòng này
        return note;
    }

    public List<Note> getPinnedUnlockedNotes(int limit) {
        List<Note> notes = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(
                "notes",
                null,
                "locked = 0 AND pinned = 1",
                null,
                null,
                null,
                "lastEdited DESC",
                String.valueOf(limit)
        );

        if (cursor.moveToFirst()) {
            do {
                notes.add(cursorToNote(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return notes;
    }

    public List<Note> getUnpinnedUnlockedNotes() {
        List<Note> notes = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(
                "notes",
                null,
                "locked = 0 AND (pinned IS NULL OR pinned = 0)",
                null,
                null,
                null,
                "lastEdited DESC"
        );

        if (cursor.moveToFirst()) {
            do {
                notes.add(cursorToNote(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return notes;
    }

    public List<Note> getPinnedUnlockedNotesSortedByLastEditedAsc() {
        List<Note> notes = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(
                "notes",
                null,
                "locked = 0 AND pinned = 1",
                null,
                null,
                null,
                "lastEdited ASC"  // Sắp xếp tăng dần theo lastEdited, note ghim lâu nhất sẽ đầu tiên
        );

        if (cursor.moveToFirst()) {
            do {
                notes.add(cursorToNote(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return notes;
    }

    public List<Note> getNotesWherePinned(boolean pinned) {
        List<Note> notes = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                "notes",
                null,
                "pinned = ?",
                new String[]{pinned ? "1" : "0"},
                null,
                null,
                "lastEdited DESC"
        );

        if (cursor.moveToFirst()) {
            do {
                notes.add(cursorToNote(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return notes;
    }


}
