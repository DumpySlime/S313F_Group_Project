package com.example.publictransportapp.model;

import android.util.Log;

public class RouteEtaModel extends RouteSearchModel{
    private String stop, dest;
    private int eta1, eta2, eta3;

    public RouteEtaModel(String route, String direction, int serviceType, String dest) {
        super(route, direction, serviceType);
        this.dest = dest;
    }

    public RouteEtaModel(String route, String direction, int serviceType, String stop, String dest, int eta1) {
        super(route, direction, serviceType);
        this.stop = stop;
        this.dest = dest;
        this.eta1 = eta1;
    }

    public RouteEtaModel(String route, String direction, int serviceType, String stop, String dest, int eta1, int eta2, int eta3) {
        super(route, direction, serviceType);
        this.stop = stop;
        this.dest = dest;
        this.eta1 = eta1;
        this.eta2 = eta2;
        this.eta3 = eta3;
    }

    public String getStop() {
        return stop;
    }

    public void setEta(int eta, int pos) {
        switch (pos) {
            case 0:
                this.eta1 = eta;
            break;
            case 1:
                this.eta2 = eta;
            break;
            case 2:
                this.eta3 = eta;
            break;
            default:
                Log.e("RouteEtaModel Error", "Eta position exceeded: eta = " + eta + "; pos = " + pos);
        }
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

    public String getDest() {
        return dest;
    }

    @Override
    public String toString() {
        return "RouteEtaModel{" +
                "stop='" + stop + '\'' +
                ", dest='" + dest + '\'' +
                ", eta1=" + eta1 +
                ", eta2=" + eta2 +
                ", eta3=" + eta3 +
                '}';
    }
}
