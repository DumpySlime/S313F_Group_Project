package com.example.publictransportapp.model;

import org.jetbrains.annotations.NotNull;

public class RouteListModel extends RouteSearchModel {

    private String orig_en,
        orig_tc,
        orig_sc,
        dest_en,
        dest_tc,
        dest_sc;

    public RouteListModel(String route, String direction, String orig_en, String orig_tc, String orig_sc, String dest_en, String dest_tc, String dest_sc, int service_type) {
        super(route, direction, service_type);
        this.orig_en = orig_en;
        this.orig_tc = orig_tc;
        this.orig_sc = orig_sc;
        this.dest_en = dest_en;
        this.dest_tc = dest_tc;
        this.dest_sc = dest_sc;
    }

    public String getOrig_en() {
        return orig_en;
    }

    public String getOrig_tc() {
        return orig_tc;
    }

    public String getOrig_sc() {
        return orig_sc;
    }

    public String getDest_en() {
        return dest_en;
    }

    public String getDest_tc() {
        return dest_tc;
    }

    public String getDest_sc() {
        return dest_sc;
    }

    @Override
    public String toString() {
        return "RouteListModel{" +
                "orig_en='" + orig_en + '\'' +
                ", orig_tc='" + orig_tc + '\'' +
                ", orig_sc='" + orig_sc + '\'' +
                ", dest_en='" + dest_en + '\'' +
                ", dest_tc='" + dest_tc + '\'' +
                ", dest_sc='" + dest_sc + '\'' +
                '}';
    }
}
