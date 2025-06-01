package com.example.noteapp.model;

public class Note {
    private int id;
    private String title;
    private String content;
    private long timestamp;     // Thời gian tạo
    private long lastEdited;    // Thời gian chỉnh sửa cuối cùng
    private boolean locked;     // Ghi chú có bị khóa hay không

    public Note() {
    }

    public Note(String title, String content, long timestamp, long lastEdited, boolean locked) {
        this.title = title;
        this.content = content;
        this.timestamp = timestamp;
        this.lastEdited = lastEdited;
        this.locked = locked;
    }

    public Note(String title, String content, long timestamp) {
        this.title = title;
        this.content = content;
        this.timestamp = timestamp;
    }


    // Getter và Setter

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

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }
}
