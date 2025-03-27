package com.example.publictransportapp.model;

public class RouteListModel extends RouteListParentModel{

    private String bound,
        orig_en,
        orig_tc,
        orig_sc,
        dest_en,
        dest_tc,
        dest_sc;
    private int service_type;

    public RouteListModel(String route, String bound, String orig_en, String orig_tc, String orig_sc, String dest_en, String dest_tc, String dest_sc, int service_type) {
        super(route);
        this.bound = bound;
        this.orig_en = orig_en;
        this.orig_tc = orig_tc;
        this.orig_sc = orig_sc;
        this.dest_en = dest_en;
        this.dest_tc = dest_tc;
        this.dest_sc = dest_sc;
        this.service_type = service_type;
    }

    public String getBound() {
        return bound;
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

    public int getService_type() {
        return service_type;
    }
}
