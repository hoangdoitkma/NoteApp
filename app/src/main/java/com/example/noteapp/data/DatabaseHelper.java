package com.example.noteapp.data;

import android.content.*;
import android.database.*;
import android.database.sqlite.*;

import com.example.noteapp.model.Expense;
import com.example.noteapp.model.Note;

import java.util.*;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "notes.db";
    private static final int DB_VERSION = 2;  // Tăng version
    private static final String TABLE = "notes";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title TEXT, " +
                "content TEXT, " +
                "timestamp LONG, " +
                "locked INTEGER DEFAULT 0, " +
                "lastEdited LONG DEFAULT 0" +
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
        if (oldV < 2) {
            db.execSQL("ALTER TABLE " + TABLE + " ADD COLUMN lastEdited LONG DEFAULT 0");
        }
    }

    public long insertNote(Note note) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("title", note.getTitle());
        cv.put("content", note.getContent());
        cv.put("timestamp", note.getTimestamp());
        cv.put("lastEdited", note.getLastEdited());  // Thêm giá trị lastEdited khi insert
        return db.insert(TABLE, null, cv);
    }

    public List<Note> getAllNotes() {
        List<Note> notes = new ArrayList<>();
        Cursor cursor = getReadableDatabase().query(TABLE, null, null, null, null, null, "timestamp DESC");
        if (cursor.moveToFirst()) {
            do {
                Note n = new Note();
                n.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                n.setTitle(cursor.getString(cursor.getColumnIndexOrThrow("title")));
                n.setContent(cursor.getString(cursor.getColumnIndexOrThrow("content")));
                n.setTimestamp(cursor.getLong(cursor.getColumnIndexOrThrow("timestamp")));
                n.setLastEdited(cursor.getLong(cursor.getColumnIndexOrThrow("lastEdited"))); // Lấy lastEdited
                notes.add(n);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return notes;
    }

    public Note getNoteById(int id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE, null, "id = ?", new String[]{String.valueOf(id)}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            Note note = new Note();
            note.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
            note.setTitle(cursor.getString(cursor.getColumnIndexOrThrow("title")));
            note.setContent(cursor.getString(cursor.getColumnIndexOrThrow("content")));
            note.setTimestamp(cursor.getLong(cursor.getColumnIndexOrThrow("timestamp")));
            note.setLastEdited(cursor.getLong(cursor.getColumnIndexOrThrow("lastEdited"))); // Lấy lastEdited
            cursor.close();
            return note;
        }
        return null;
    }

    public void deleteNote(int noteId) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE, "id=?", new String[]{String.valueOf(noteId)});
    }

    public int updateNote(Note note) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("title", note.getTitle());
        cv.put("content", note.getContent());
        cv.put("timestamp", note.getTimestamp());
        cv.put("lastEdited", note.getLastEdited());
        return db.update(TABLE, cv, "id = ?", new String[]{String.valueOf(note.getId())});
    }
    public List<Expense> getAllExpenses() {
        List<Expense> expenses = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM expenses ORDER BY date DESC", null);

        if (cursor.moveToFirst()) {
            do {
                Expense expense = new Expense();
                expense.setId(cursor.getLong(cursor.getColumnIndexOrThrow("id")));
                expense.setAmount(cursor.getDouble(cursor.getColumnIndexOrThrow("amount")));
                expense.setDate(cursor.getString(cursor.getColumnIndexOrThrow("date")));
                expense.setCategory(cursor.getString(cursor.getColumnIndexOrThrow("category")));
                expense.setNote(cursor.getString(cursor.getColumnIndexOrThrow("note")));
                expenses.add(expense);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return expenses;
    }
    public List<Expense> getExpensesByDate(String date) {
        List<Expense> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT id, amount, category, date, note FROM expenses WHERE date = ? ORDER BY id DESC",
                new String[]{date}
        );

        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            double amount = cursor.getDouble(1);
            String category = cursor.getString(2);
            String dateStr = cursor.getString(3);
            String note = cursor.getString(4);

            Expense expense = new Expense(id, amount, category, dateStr, note);
            list.add(expense);
        }
        cursor.close();
        return list;
    }

}
