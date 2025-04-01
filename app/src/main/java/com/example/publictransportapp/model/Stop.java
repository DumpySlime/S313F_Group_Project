package com.example.publictransportapp.model;


import com.google.gson.annotations.SerializedName;

public class Stop {
    @SerializedName("stop")
    private String stop;
    @SerializedName("name_en")
    private String nameEn;
    private double lat;
    @SerializedName("long")
    private double lon;

    public Stop(String stop, String nameEn, double lat, double lon) {
        this.stop = stop;
        this.nameEn = nameEn;
        this.lat = lat;
        this.lon = lon;
    }

    public double distanceTo(double otherLat, double otherLon) {
        final double R = 6371e3; // meters
        double φ1 = Math.toRadians(lat);
        double φ2 = Math.toRadians(otherLat);
        double Δφ = Math.toRadians(otherLat - lat);
        double Δλ = Math.toRadians(otherLon - lon);

        double a = Math.sin(Δφ / 2) * Math.sin(Δφ / 2) +
                Math.cos(φ1) * Math.cos(φ2) *
                        Math.sin(Δλ / 2) * Math.sin(Δλ / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    // Getters
    public String getStop() { return stop; }
    public String getNameEn() { return nameEn; }
    public double getLat() { return lat; }
    public double getLon() { return lon; }
}