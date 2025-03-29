package com.example.publictransportapp.model;

public class RouteStopListModel extends RouteEtaModel {

    private String sequence;

    public RouteStopListModel(String route, String direction, int serviceType, String sequence, String stopId) {
        super(route, direction, serviceType);
        super.setStopId(stopId);
        this.sequence = sequence;
    }

    public String getSequence() {
        return sequence;
    }

    @Override
    public String toString() {
        return super.toString() + "RouteStopListModel{" +
                "sequence='" + sequence +
                '}';
    }
}
