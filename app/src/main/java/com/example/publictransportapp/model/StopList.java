package com.example.publictransportapp.model;

import java.util.ArrayList;
import java.util.HashMap;

public class StopList {
    public static String
            STOPID = "stopId",
            NAME_EN = "name_en",
            NAME_TC = "name_tc",
            NAME_SC = "name_sc",
            LAT = "lat",
            LONG = "long";

    public static ArrayList<HashMap<String, String>> stopList = new ArrayList<>();

    public static void addStopList(String stopId, String name_en, String name_tc, String name_sc, String lat, String lon) {
        HashMap<String, String> stopData = new HashMap<>();
        stopData.put(STOPID, stopId);
        stopData.put(NAME_EN, name_en);
        stopData.put(NAME_TC, name_tc);
        stopData.put(NAME_SC, name_sc);
        stopData.put(LAT, lat);
        stopData.put(LONG, lon);
    }

}
