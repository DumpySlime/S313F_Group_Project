package com.example.publictransportapp.model;

public class Place {
    private String location;
    private String category;

    public Place(String location, String category) {
        this.location = location;
        this.category = category;
    }

    // Getters and Setters
    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}

