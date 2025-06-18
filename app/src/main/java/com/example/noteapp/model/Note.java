package com.example.noteapp.model;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "notes")
public class Note implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String title;
    private String content; // Nội dung HTML

    private long timestamp;     // Thời điểm tạo
    private long lastEdited;    // Thời điểm chỉnh sửa cuối

    private boolean pinned;     // Ghi chú được ghim
    private boolean locked;     // Ghi chú bị khóa
    private String tag;         // Tag phân loại (nếu dùng)

    // === Constructors ===
    public Note() {
        // Constructor mặc định cho Firebase & Room
    }
    @Ignore
    public Note(String title, String content, long timestamp, long lastEdited) {
        this.title = title;
        this.content = content;
        this.timestamp = timestamp;
        this.lastEdited = lastEdited;
        this.pinned = false;
        this.locked = false;
        this.tag = null;
    }

    public Note(String title, String content, long time, boolean locked, boolean pinned) {
        this.title = title;
        this.content = content;
        this.timestamp = time;
        this.lastEdited = time;
        this.locked = locked;
        this.pinned = pinned;
        this.tag = null;
    }
    // === Getters & Setters ===
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getLastEdited() {
        return lastEdited;
    }

    public void setLastEdited(long lastEdited) {
        this.lastEdited = lastEdited;
    }

    public boolean isPinned() {
        return pinned;
    }

    public void setPinned(boolean pinned) {
        this.pinned = pinned;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
