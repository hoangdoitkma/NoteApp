package com.example.noteapp.model;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {QRCode.class}, version = 1)
public abstract class QRCodeDatabase extends RoomDatabase {
    private static QRCodeDatabase instance;
    public abstract QRCodeDao qrCodeDao();

    public static synchronized QRCodeDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            QRCodeDatabase.class, "qr_code_database")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}
