package com.example.publictransportapp.model;

public class Stop {
    private String routeNumber;
    private String distinction;
    private String stopCategory;

    public Stop(String routeNumber, String distinction, String stopCategory) {
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
}
