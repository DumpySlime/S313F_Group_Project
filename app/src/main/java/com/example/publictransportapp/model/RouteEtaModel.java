package com.example.publictransportapp.model;

import android.util.Log;

public class RouteEtaModel extends RouteSearchModel{
    private String stopId, destStopId;
    private int eta1, eta2, eta3;

    public RouteEtaModel(String route, String direction, int serviceType) {
        super(route, direction, serviceType);
    }

    public RouteEtaModel(String route, String direction, int serviceType, String destStopId) {
        super(route, direction, serviceType);
        this.destStopId = destStopId;
    }

    public RouteEtaModel(String route, String direction, int serviceType, String stopId, String destStopId, int eta1) {
        super(route, direction, serviceType);
        this.stopId = stopId;
        this.destStopId = destStopId;
        this.eta1 = eta1;
    }

    public RouteEtaModel(String route, String direction, int serviceType, String stopId, String destStopId, int eta1, int eta2, int eta3) {
        super(route, direction, serviceType);
        this.stopId = stopId;
        this.destStopId = destStopId;
        this.eta1 = eta1;
        this.eta2 = eta2;
        this.eta3 = eta3;
    }

    public String getStopId() {
        return stopId;
    }

    public void setStopId(String stopId) {
        this.stopId = stopId;
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

    public String getDestStopId() {
        return destStopId;
    }

    @Override
    public String toString() {
        return super.toString() + "RouteEtaModel{" +
                "stop='" + stopId + '\'' +
                ", dest='" + destStopId + '\'' +
                ", eta1=" + eta1 +
                ", eta2=" + eta2 +
                ", eta3=" + eta3 +
                '}';
    }
}
