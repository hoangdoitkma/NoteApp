package com.example.noteapp.model;

public class Task {
    private int id;
    private String title;
    private String description;
    private long dueTimeMillis;
    private boolean completed;
    private String repeatDays; // Lưu dạng "2,3,4" nghĩa là lặp lại thứ 2,3,4

    public Task() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    private String ringtoneUri;  // trong model Task

    public String getRingtoneUri() {
        return ringtoneUri;
    }

    public void setRingtoneUri(String ringtoneUri) {
        this.ringtoneUri = ringtoneUri;
    }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public long getDueTimeMillis() { return dueTimeMillis; }
    public void setDueTimeMillis(long dueTimeMillis) { this.dueTimeMillis = dueTimeMillis; }

    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }

    public String getRepeatDays() { return repeatDays; }
    public void setRepeatDays(String repeatDays) { this.repeatDays = repeatDays; }
}
