package com.example.publictransportapp.model;

import java.util.Objects;

public class StopObject {
    private String routeNumber;
    private String distinction;
    private String stopCategory;

    public StopObject(String routeNumber, String distinction, String stopCategory) {
        this.routeNumber = routeNumber;
        this.distinction = distinction;
        this.stopCategory = stopCategory;
    }

    // Getters and Setters
    public String getRouteNumber() {
        return routeNumber;
    }

    public void setRouteNumber(String routeNumber) {
        this.routeNumber = routeNumber;
    }

    public String getDistinction() {
        return distinction;
    }

    public void setDistinction(String distinction) {
        this.distinction = distinction;
    }

    public String getStopCategory() {
        return stopCategory;
    }

    public void setStopCategory(String stopCategory) {
        this.stopCategory = stopCategory;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        StopObject that = (StopObject) obj;
        return routeNumber.equals(that.routeNumber) &&
                distinction.equals(that.distinction) &&
                stopCategory.equals(that.stopCategory);
    }

    @Override
    public int hashCode() {
        return Objects.hash(routeNumber, distinction, stopCategory);
    }
}
