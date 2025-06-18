package com.example.noteapp.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.Ignore;

@Entity
public class QRCode {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private String imagePath;

    // Room sẽ sử dụng constructor này để tạo instance từ DB
    public QRCode(String title, String imagePath) {
        this.title = title;
        this.imagePath = imagePath;
    }

    // Constructor này chỉ dùng cho code, KHÔNG dùng cho Room
    @Ignore
    public QRCode(int id, String title, String imagePath) {
        this.id = id;
        this.title = title;
        this.imagePath = imagePath;
    }

    // Getter và Setter
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
}
