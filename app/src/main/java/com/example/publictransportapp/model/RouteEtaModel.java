package com.example.publictransportapp.model;

public class RouteEtaModel extends RouteSearchModel{
    private String stop;
    private int eta1, eta2, eta3;

    public RouteEtaModel(String route, String direction, int serviceType, String stop, int eta1, int eta2, int eta3) {
        super(route, direction, serviceType);
        this.stop = stop;
        this.eta1 = eta1;
        this.eta2 = eta2;
        this.eta3 = eta3;
    }

    public String getStop() {
        return stop;
    }

    public int getEta1() {
        return eta1;
    }

    public int getEta2() {
        return eta2;
    }

    public int getEta3() {
        return eta3;
    }
}
