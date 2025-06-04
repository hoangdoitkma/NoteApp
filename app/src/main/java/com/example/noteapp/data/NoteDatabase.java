package com.example.noteapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class NoteDatabase extends SQLiteOpenHelper {

    public static final String DB_NAME = "notes.db";
    public static final int DB_VERSION = 2;

    public NoteDatabase(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE notes (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title TEXT, " +
                "content TEXT, " +
                "timestamp LONG, " +
                "lastEdited LONG, " +
                "locked INTEGER DEFAULT 0, " +
                "pinned INTEGER DEFAULT 0" +  // Thêm cột pinned với giá trị mặc định
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            // Thêm cột pinned nếu chưa có
            try {
                db.execSQL("ALTER TABLE notes ADD COLUMN pinned INTEGER DEFAULT 0");
            } catch (Exception e) {
                e.printStackTrace(); // hoặc xử lý lỗi phù hợp
            }
        }
    }
}