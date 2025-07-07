package com.example.zenaura;

public class Label {
    private String name;
    private String color;

    public Label() {
        // Required for Firestore deserialization
    }

    public Label(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(Object color) {
        // Accept both Long and String from Firestore
        if (color instanceof Long) {
            long colorLong = (Long) color;
            this.color = String.format("#%08X", colorLong);
        } else if (color instanceof String) {
            this.color = (String) color;
        } else {
            this.color = "#000000"; // fallback to black
        }
    }

}
