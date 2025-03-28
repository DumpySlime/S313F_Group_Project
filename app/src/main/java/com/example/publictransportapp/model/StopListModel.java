package com.example.publictransportapp.model;

import androidx.annotation.NonNull;

public class StopListModel {
    private String stopId,
        name_en,
        name_tc,
        name_sc;
    private double lat,
        lon;

    public StopListModel(String stopId, String name_en, String name_tc, String name_sc, double lat, double lon) {
        this.stopId = stopId;
        this.name_en = name_en;
        this.name_tc = name_tc;
        this.name_sc = name_sc;
        this.lat = lat;
        this.lon = lon;
    }

    public String getStopId() {
        return stopId;
    }

    public String getName_en() {
        return name_en;
    }

    public String getName_tc() {
        return name_tc;
    }

    public String getName_sc() {
        return name_sc;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    @Override
    public String toString() {
        return "StopListModel{" +
                "stopId='" + stopId + '\'' +
                ", name_en='" + name_en + '\'' +
                ", name_tc='" + name_tc + '\'' +
                ", name_sc='" + name_sc + '\'' +
                ", lat=" + lat +
                ", lon=" + lon +
                '}';
    }
}
