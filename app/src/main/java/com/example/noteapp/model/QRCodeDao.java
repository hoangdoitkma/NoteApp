package com.example.noteapp.model;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface QRCodeDao {
    @Insert
    void insert(QRCode qrCode);

    @Update
    void update(QRCode qrCode);

    @Delete
    void delete(QRCode qrCode);

    @Query("SELECT * FROM QRCode ORDER BY id DESC")
    LiveData<List<QRCode>> getAll();
}
