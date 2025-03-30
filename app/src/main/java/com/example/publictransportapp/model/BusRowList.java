package com.example.publictransportapp.model;

import java.util.ArrayList;
import java.util.HashMap;

public class BusRowList {
    public static String
            ROUTE = "route",
            STOP_NAME = "stop_name",
            DEST = "dest",
            ETA = "eta",
            SERVICE_TYPE = "service_type",
            DIRECTION = "direction";

    public static ArrayList<HashMap<String, String>> busRowList = new ArrayList<>();

    public static void addBusList(String route, String stop_name, String dest, String eta, String serviceType, String direction) {
        HashMap<String, String> busRow = new HashMap<>();
        busRow.put(ROUTE, route);
        busRow.put(STOP_NAME, stop_name);
        busRow.put(DEST, dest);
        busRow.put(ETA, eta);
        busRow.put(SERVICE_TYPE, serviceType);
        busRow.put(DIRECTION, direction);

        busRowList.add(busRow);
    }
}
