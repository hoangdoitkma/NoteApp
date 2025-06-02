package com.example.noteapp.model;

public class Expense {
    private long id;
    private double amount;
    private String date;
    private String category;
    private String note;
    private String type;      // Tiền ăn, nhà,...

    public Expense() {}

    public Expense(double amount, String category, String date, String note) {
        this.amount = amount;
        this.category = category;
        this.date = date;
        this.note = note;
    }
    public Expense(int id, double amount, String category, String dateStr, String note) {
        this.id = id;
        this.amount = amount;
        this.category = category;
        this.date = dateStr;
        this.note = note;
    }

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getAmount() {
        return amount;
    }
    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }

    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }

    public String getNote() {
        return note;
    }
    public void setNote(String note) {
        this.note = note;
    }
}
