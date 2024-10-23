package com.example.moneymanagement.Model;

public class Model {
    private String title;
    private String date;
    private String time;

    public Model(String title, String date, String time) {
        this.title = title;
        this.date = date;
        this.time = time;
    }

    // Getter method for title
    public String getTitle() {
        return title;
    }

    // Setter method for title
    public void setTitle(String title) {
        this.title = title;
    }

    // Getter method for date
    public String getDate() {
        return date;
    }

    // Setter method for date
    public void setDate(String date) {
        this.date = date;
    }

    // Getter method for time
    public String getTime() {
        return time;
    }

    // Setter method for time
    public void setTime(String time) {
        this.time = time;
    }
}
