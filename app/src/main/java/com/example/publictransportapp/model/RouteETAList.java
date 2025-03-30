package com.example.publictransportapp.model;

import java.util.ArrayList;
import java.util.HashMap;

public class RouteETAList {
    public static String
            CO = "co",
            ROUTE = "route",
            DIRECTION = "direction",
            SERVICETYPE = "serviceType",
            SEQ = "seq",
            DEST_EN = "dest_en",
            DEST_TC = "dest_tc",
            DEST_SC = "dest_sc",
            ETA_SEQ = "eta_seq";

    public static ArrayList<HashMap<String, String>> etaList = new ArrayList<>();

    public static void addETAList(String co, String route, String direction, String service_type, String seq, String dest_en, String dest_tc, String dest_sc, String eta_seq) {
        HashMap<String, String> etaData = new HashMap<>();
        etaData.put(CO, co);
        etaData.put(ROUTE, route);
        etaData.put(DIRECTION, direction);
        etaData.put(SERVICETYPE, service_type);
        etaData.put(SEQ, seq);
        etaData.put(DEST_EN, dest_en);
        etaData.put(DEST_TC, dest_tc);
        etaData.put(DEST_SC, dest_sc);
        etaData.put(ETA_SEQ, eta_seq);

        etaList.add(etaData);
    }

    public static void clearETAList() {
        etaList.clear();
    }
}
