package com.example.publictransportapp.model;

public class BusRowModel {
    private String stop,
        dest,
        route;
    private int eta;

    public BusRowModel(String stop, String dest, String route, int eta) {
        this.stop = stop;
        this.dest = dest;
        this.route = route;
        this.eta = eta;
    }

    public String getStop() {
        return stop;
    }

    public String getDest() {
        return dest;
    }

    public String getRoute() {
        return route;
    }

    public int getEta() {
        return eta;
    }
}
