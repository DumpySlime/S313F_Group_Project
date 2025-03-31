package com.example.publictransportapp;


import com.example.publictransportapp.model.BusRoute;

import org.json.JSONException;
import org.json.JSONObject;

public class JsonParser {
    public static BusRoute parseBusRoute(String jsonResponse) throws JSONException {
        JSONObject jsonObject = new JSONObject(jsonResponse);
        JSONObject data = jsonObject.getJSONObject("data");

        String routeNumber = data.getString("route");
        String origTc = data.getString("orig_tc");
        String destTc = data.getString("dest_tc");
        String origEn = data.getString("orig_en");
        String destEn = data.getString("dest_en");



        return new BusRoute(routeNumber,origTc, destTc, origEn, destEn);
    }
}
