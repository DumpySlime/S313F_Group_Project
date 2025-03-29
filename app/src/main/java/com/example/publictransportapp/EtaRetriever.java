package com.example.publictransportapp;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.publictransportapp.model.RouteEtaModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

public class EtaRetriever {
    private RequestQueue requestQueue;
    private static final String ETA_URL = "https://data.etabus.gov.hk/v1/transport/kmb/eta/";

    public EtaRetriever(RequestQueue requestQueue) {
        this.requestQueue = requestQueue;
    }

    public void getEta(String stopId, String routeId, int serviceType, String dest, String direction, EtaCallback callback) {
        String ROUTE_STOP_ETA_URL = ETA_URL + stopId + "/" + routeId + "/" + serviceType;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, ROUTE_STOP_ETA_URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject res) {
                        try {
                            // Log.d("in EtaRetriever", "Url passing: " + ROUTE_STOP_ETA_URL);
                            // Log.d("in EtaRetriever", "APIResponse: " + res.toString());
                            JSONArray etaData = res.getJSONArray("data");
                            if (etaData.length() > 0) {
                                Log.d("in EtaRetriever", "not null data url passing: " + ROUTE_STOP_ETA_URL);
                                Log.d("in EtaRetriever", "APIResponse: " + etaData.toString());
                                int j = 0;
                                for (int i = 0; (i < etaData.length()) && (j < 3); i++) {
                                    JSONObject eta = etaData.getJSONObject(i);
                                    String etaString = eta.getString("eta");
                                    String routeDest = eta.getString("dest_en");
                                    Log.d("ETA String", "etaString: " + etaString); // Log etaString value

                                    // Check if etaString is not null and not empty
                                    if ((routeDest.equals(dest)) && (!etaString.equals("null"))) {
                                        Log.d("in EtaRetriever", "Valid ETA found: " + etaString);
                                        Log.d("in EtaRetriever", "Valid Destination found: " + routeDest);

                                        // Calculate minutes until arrival
                                        OffsetDateTime offsetDateTime = OffsetDateTime.parse(etaString);
                                        LocalDateTime etaDateTime = offsetDateTime.atZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
                                        long minutesUntilArrival = ChronoUnit.MINUTES.between(LocalDateTime.now(), etaDateTime);

                                        Log.d("in EtaRetriever", "i: " + i + " j: " + j + "; etaData_length: " + etaData.length());
                                        Log.d("in EtaRetriever", eta.getString("route") + "(" + routeDest + ")" + "(" + stopId + ")" + ": " + i + "- " + minutesUntilArrival);
                                        RouteEtaModel routeEtaModel = new RouteEtaModel(routeId, direction, serviceType, dest);
                                        routeEtaModel.setEta((int) minutesUntilArrival, j);
                                        callback.onEtaReceived(routeEtaModel);
                                        j++;
                                    } else {
                                        Log.d("in EtaRetriever", "Skipping entry due to null or empty ETA: " + etaString);
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            callback.onError("JSON parsing error: " + e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callback.onError("Error fetching ETA: " + error.getMessage());
                    }
                });

        requestQueue.add(jsonObjectRequest);
    }
}