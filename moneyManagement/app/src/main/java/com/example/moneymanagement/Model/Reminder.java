package com.example.moneymanagement.Model;

public class Reminder {
    private String title;
    private String date;
    private String time;
    private String userId; // New field to store user ID

    // Required default constructor for Firebase deserialization
    public Reminder() {
    }

    public Reminder(String title, String date, String time, String userId) {
        this.title = title;
        this.date = date;
        this.time = time;
        this.userId = userId; // Set user ID
    }

    // Getters and setters for Firebase serialization
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
