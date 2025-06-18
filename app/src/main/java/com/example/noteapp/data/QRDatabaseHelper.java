package com.example.noteapp.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.noteapp.model.QRCode;

import java.util.ArrayList;
import java.util.List;

public class QRDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "qrcode_db";
    private static final int DATABASE_VERSION = 2;

    private static final String TABLE_QRCODES = "qr_codes";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_IMAGE_PATH = "image_path";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_CREATED_AT = "created_at";

    public QRDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_QRCODES + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_IMAGE_PATH + " TEXT NOT NULL," +
                COLUMN_NAME + " TEXT," +
                COLUMN_CREATED_AT + " INTEGER)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Xóa bảng cũ nếu tồn tại và tạo lại bảng mới
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_QRCODES);
        onCreate(db);
    }

    public long insertQRCode(String name, String imagePath, long createdAt) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_IMAGE_PATH, imagePath);
        values.put(COLUMN_CREATED_AT, createdAt);
        return db.insert(TABLE_QRCODES, null, values);
    }

    public List<QRCode> getAllQRCodes() {
        List<QRCode> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_QRCODES,
                    new String[]{COLUMN_ID, COLUMN_IMAGE_PATH, COLUMN_NAME},
                    null, null, null, null, COLUMN_CREATED_AT + " DESC");

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
                    String path = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_PATH));
                    String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME));
                    list.add(new QRCode(name, path));
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return list;
    }

    public int deleteQRCode(int id) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(TABLE_QRCODES, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
    }

    public int updateQRCode(QRCode qrCode) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, qrCode.getTitle());
        values.put(COLUMN_IMAGE_PATH, qrCode.getImagePath());
        return db.update(TABLE_QRCODES, values, COLUMN_ID + " = ?", new String[]{String.valueOf(qrCode.getId())});
    }
}

