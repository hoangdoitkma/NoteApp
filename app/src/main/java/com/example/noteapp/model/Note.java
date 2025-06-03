/* Note.java */
package com.example.noteapp.model;

public class Note {
    private int id;
    private String title;
    private String content;
    private long timestamp;
    private long lastEdited;
    private boolean locked;

    public Note() {}

    public Note(String title, String content, long timestamp, long lastEdited, boolean locked) {
        this.title = title;
        this.content = content;
        this.timestamp = timestamp;
        this.lastEdited = lastEdited;
        this.locked = locked;
    }

    public Note(String title, String content, long l) {
        this.title = title;
        this.content = content;
        this.timestamp = l;
        this.lastEdited = l;
        this.locked = false;
    }

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
    private boolean pinned;

    public boolean isPinned() {
        return pinned;
    }

    public void setPinned(boolean pinned) {
        this.pinned = pinned;
    }

// Getters and setters
    // ... (omitted for brevity)
}