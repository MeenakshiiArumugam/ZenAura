package com.example.zenaura;

public class User {
    private String name;
    private String mail;

    public User() {
        // Default constructor required for Firestore
    }

    public User(String name, String mail) {
        this.name = name;
        this.mail = mail;
    }

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getMail() { return mail; }
    public void setMail(String mail) { this.mail = mail; }
}
