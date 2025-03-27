package com.example.publictransportapp.model;

public class RouteSearchModel {
    private String route,
        direction;
    private int serviceType;

    public RouteSearchModel(String route, String direction, int serviceType) {
        this.route = route;
        this.direction = direction;
        this.serviceType = serviceType;
    }

    public String getRoute() {
        return route;
    }

    public String getDirection() {
        return direction;
    }

    public int getServiceType() {
        return serviceType;
    }
}
