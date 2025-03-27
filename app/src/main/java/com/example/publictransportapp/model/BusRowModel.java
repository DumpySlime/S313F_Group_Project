package com.example.publictransportapp.model;

public class BusRowModel extends RouteSearchModel {
    private String stop,
        dest;
    private int eta;

    public BusRowModel(String stop, String dest, String route, int eta, String direction, int serviceType) {
        super(route, direction, serviceType);
        this.stop = stop;
        this.dest = dest;
        this.eta = eta;
    }

    public String getStop() {
        return stop;
    }

    public String getDest() {
        return dest;
    }

    public int getEta() {
        return eta;
    }
}
