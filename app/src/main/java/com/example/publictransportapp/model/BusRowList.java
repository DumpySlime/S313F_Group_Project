package com.example.publictransportapp.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BusRowList{
    public static String
            ROUTE = "route",
            STOP_NAME = "stop_name",
            DEST = "dest",
            ETA = "eta",
            SERVICE_TYPE = "service_type",
            DIRECTION = "direction",
            STOP_ID = "stop_id";
    public static String
            LAT = "lat",
            LON = "lon";

    public static List<HashMap<String, String>> busRowList = new ArrayList<>();

    public static void addBusList(String route, String stop_name, String dest, String eta, String serviceType, String direction, String stop_id, String lat, String lon) {
        if (notExist(route, stop_id, serviceType, dest)) {
            HashMap<String, String> busRow = new HashMap<>();
            busRow.put(ROUTE, route);
            busRow.put(STOP_NAME, stop_name);
            busRow.put(DEST, dest);
            busRow.put(ETA, eta);
            busRow.put(SERVICE_TYPE, serviceType);
            busRow.put(DIRECTION, direction);
            busRow.put(STOP_ID, stop_id);
            busRow.put(LAT, lat);
            busRow.put(LON, lon);

            busRowList.add(busRow);
        }
    }

    public static boolean notExist(String route, String stopId, String serviceType, String dest) {
        for (HashMap<String, String> busRow : busRowList) {
            if (busRow.get(ROUTE).equals(route) &&
                    busRow.get(STOP_ID).equals(stopId) &&
                    busRow.get(SERVICE_TYPE).equals(serviceType) &&
                    busRow.get(DEST).equals(dest)) {
                return false; // Duplicate found
            }
        }
        return true;
    }

    public static void clearBusList() {
        busRowList.clear();
    }
}
