package com.example.publictransportapp.model;

import java.util.Objects;

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

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Place that = (Place) obj;
        return location.equals(that.location) && category.equals(that.category);
    }

    @Override
    public int hashCode() {
        return Objects.hash(location, category);
    }
}

