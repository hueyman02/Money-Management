package com.example.moneymanagement.Model;

// User.java
public class User {
    private String uid;
    private String password;

    public User(String uid) {
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

