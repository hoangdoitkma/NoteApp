package com.example.noteapp.model;

public class DailyExpenseSummary {
    private final String date;
    private final String dayOfWeek;
    private final double total;

    public DailyExpenseSummary(String date, String dayOfWeek, double total) {
        this.date = date;
        this.dayOfWeek = dayOfWeek;
        this.total = total;
    }

    public String getDate() {
        return date;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public double getTotal() {
        return total;
    }
}
