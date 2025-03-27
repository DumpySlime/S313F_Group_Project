package com.example.publictransportapp;

import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.publictransportapp.model.EtaRetrieverModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

public interface EtaRetriever {
    String ROUTE_STOP_ETA_URL = "https://data.etabus.gov.hk/v1/transport/kmb/eta/";

    static EtaRetrieverModel getRouteStopETA(String stopId, String routeId, int serviceType, View view, RequestQueue requestQueue) {
        EtaRetrieverModel etas = new EtaRetrieverModel();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, ROUTE_STOP_ETA_URL + stopId + "/" + routeId + "/" + serviceType, null,
                res -> {
                    try {
                        JSONArray etaData = res.getJSONArray("data");
                        // get the 3 eta and change its format to remaining time (in minutes)
                        for (int i = 0; i < etaData.length(); i++) {
                            JSONObject eta = etaData.getJSONObject(i);
                            String etaString = eta.getString("eta");
                            // calculate minutes until arrival
                            OffsetDateTime offsetDateTime = OffsetDateTime.parse(etaString);
                            LocalDateTime etaDateTime = offsetDateTime.atZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
                            long minutesUntilArrival = ChronoUnit.MINUTES.between(LocalDateTime.now(), etaDateTime);
                            etas.setEta((int) minutesUntilArrival, i);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(view.getContext(), "Error parsing stop data", Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(view.getContext(), "Error fetching stop data", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(jsonObjectRequest);
        return etas;
    }
}
