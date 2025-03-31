package com.example.publictransportapp.model;

import java.util.ArrayList;
import java.util.HashMap;

public class RouteRowList {
    public static String
            STOP_NAME = "stop_name",
            ETA1 = "eta1",
            ETA2 = "eta2",
            ETA3 = "eta3",
            STOP_ID = "stop_id";

    public static ArrayList<HashMap<String, String>> routeRowList = new ArrayList<>();

    public static void addRouteRowList(String stop_name, String eta1, String eta2, String eta3, String stop_id) {
        HashMap<String, String> routeRow = new HashMap<>();
        routeRow.put(STOP_NAME, stop_name);
        routeRow.put(ETA1, eta1);
        routeRow.put(ETA2, eta2);
        routeRow.put(ETA3, eta3);
        routeRow.put(STOP_ID, stop_id);

        routeRowList.add(routeRow);
    }

    public static void clearRouteRowList() {
        routeRowList.clear();
    }
}
