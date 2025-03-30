package com.example.publictransportapp.model;

import java.util.ArrayList;
import java.util.HashMap;

public class RouteStopList {

    public static String
            ROUTE = "route",
            DIRECTION = "direction",
            SERVICETYPE = "serviceType",
            SEQ = "seq",
            STOPID = "stopId";

    public static ArrayList<HashMap<String, String>> routeStopList = new ArrayList<>();

    public static void addRouteStopList(String route, String direction, String serviceType, String seq, String stopId) {
        HashMap<String, String> routeStopData = new HashMap<>();
        routeStopData.put(ROUTE, route);
        routeStopData.put(DIRECTION, direction);
        routeStopData.put(SERVICETYPE, serviceType);
        routeStopData.put(SEQ, seq);
        routeStopData.put(STOPID, stopId);

        routeStopList.add(routeStopData);
    }
}
