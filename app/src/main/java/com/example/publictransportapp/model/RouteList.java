package com.example.publictransportapp.model;

import java.util.ArrayList;
import java.util.HashMap;

public class RouteList {

    public static String
            ROUTE = "route",
            DIRECTION = "direction",
            SERVICETYPE = "serviceType",
            ORIG_EN = "orig_en",
            ORIG_TC = "orig_tc",
            ORIG_SC = "orig_sc",
            DEST_EN = "dest_en",
            DEST_TC = "dest_tc",
            DEST_SC = "dest_sc";

    public static ArrayList<HashMap<String, String>> routeList = new ArrayList<>();

    public static void addRouteList(String route, String direction, String service_type, String orig_en, String orig_tc, String orig_sc, String dest_en, String dest_tc, String dest_sc) {
        HashMap<String, String> routeData = new HashMap<>();
        routeData.put(ROUTE, route);
        routeData.put(DIRECTION, direction);
        routeData.put(SERVICETYPE, service_type);
        routeData.put(ORIG_EN, orig_en);
        routeData.put(ORIG_TC, orig_tc);
        routeData.put(ORIG_SC, orig_sc);
        routeData.put(DEST_EN, dest_en);
        routeData.put(DEST_TC, dest_tc);
        routeData.put(DEST_SC, dest_sc);

        routeList.add(routeData);
    }
}
