// QrDatabaseHelper.java
package com.example.noteapp.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class QrDatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "NoteAppDB";
    private static final int DB_VERSION = 1;

    private static final String TABLE_QR = "qr_images";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_IMAGE_URI = "image_uri";

    public QrDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_QR + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_IMAGE_URI + " TEXT NOT NULL)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_QR);
        onCreate(db);
    }

    public void insertImage(String imageUri) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_IMAGE_URI, imageUri);
        db.insert(TABLE_QR, null, values);
        db.close();
    }

    public List<String> getAllImageUris() {
        List<String> uris = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_QR, null);
        if (cursor.moveToFirst()) {
            do {
                uris.add(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_URI)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return uris;
    }
}
