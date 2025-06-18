package com.example.noteapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteException;

public class NoteDatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "notes.db";
    private static final int DB_VERSION = 3; // Tăng version lên 3
    private static NoteDatabaseHelper instance;
    private static final String TABLE_NOTES = "notes";

    private NoteDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public static synchronized NoteDatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new NoteDatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createNotesTable = "CREATE TABLE " + TABLE_NOTES + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title TEXT, " +
                "content TEXT, " +
                "timestamp LONG, " +
                "locked INTEGER DEFAULT 0, " +
                "lastEdited LONG DEFAULT 0, " +
                "pinned INTEGER DEFAULT 0" +  // Thêm cột pinned
                ")";
        db.execSQL(createNotesTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            try {
                db.execSQL("ALTER TABLE " + TABLE_NOTES + " ADD COLUMN locked INTEGER DEFAULT 0");
            } catch (SQLiteException ignored) {}
            try {
                db.execSQL("ALTER TABLE " + TABLE_NOTES + " ADD COLUMN lastEdited LONG DEFAULT 0");
            } catch (SQLiteException ignored) {}
        }
        if (oldVersion < 3) {
            try {
                db.execSQL("ALTER TABLE " + TABLE_NOTES + " ADD COLUMN pinned INTEGER DEFAULT 0");
            } catch (SQLiteException ignored) {}
        }
    }
}

