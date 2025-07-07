package com.example.zenaura;

import java.util.Date;

public class Note {
    private String title;
    private String description;
    private String userId;
    private Date timestamp;
    private String id;

    private Label label;

    private String labelName;   // user-defined label
    private String labelColor;
    public String getLabelColor() {
        return labelColor;
    }

    public String getLabelName() {
        return labelName;
    }
    public Note() {
        // Needed for Firestore serialization
    }

    public Note(String title, String description) {
        this.title = title;
        this.description = description;
        this.timestamp = new Date();
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public Label getLabel() {
        return label;
    }

    public void setLabel(Label label) {
        this.label = label;
    }
}
