package com.example.publictransportapp;

import android.util.Log;

import com.example.publictransportapp.model.RouteList;
import com.example.publictransportapp.model.RouteStopList;
import com.example.publictransportapp.model.StopList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class JsonHandlerThread extends Thread {
    private static final String TAG = "JsonHandlerThread";
    private static String routeUrl = "https://data.etabus.gov.hk/v1/transport/kmb/route/",
            stopUrl = "https://data.etabus.gov.hk/v1/transport/kmb/stop/",
            routeStopUrl = "https://data.etabus.gov.hk/v1/transport/kmb/route-stop/";

    public static String makeRouteRequest() {
        String response = null;

        try {
            URL url = new URL(routeUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            // Read the response
            InputStream in = new BufferedInputStream(conn.getInputStream());
            response = inputStreamToString(in);
        } catch (ProtocolException e) {
            Log.e(TAG, "ProtocolException: " + e.getMessage());
        } catch (MalformedURLException e) {
            Log.e(TAG, "MalformedURLException: " + e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, "IOException: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
        return response;
    }

    public static String makeStopRequest() {
        String response = null;

        try {
            URL url = new URL(stopUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            // Read the response
            InputStream in = new BufferedInputStream(conn.getInputStream());
            response = inputStreamToString(in);
        } catch (ProtocolException e) {
            Log.e(TAG, "ProtocolException: " + e.getMessage());
        } catch (MalformedURLException e) {
            Log.e(TAG, "MalformedURLException: " + e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, "IOException: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
        return response;
    }

    public static String makeRouteStopRequest() {
        String response = null;

        try {
            URL url = new URL(routeStopUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            // Read the response
            InputStream in = new BufferedInputStream(conn.getInputStream());
            response = inputStreamToString(in);
        } catch (ProtocolException e) {
            Log.e(TAG, "ProtocolException: " + e.getMessage());
        } catch (MalformedURLException e) {
            Log.e(TAG, "MalformedURLException: " + e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, "IOException: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
        return response;
    }

    private static String inputStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = "";
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (IOException e) {
            Log.e(TAG, "IOException: " + e.getMessage());
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                Log.e(TAG, "IOException: " + e.getMessage());
            }
        }
        return sb.toString();
    }

    public void run() {
        fetchRouteList();
        fetchStopList();
        fetchRouteStopList();
    }
    
    private void fetchRouteList() {
        String routeStr = makeRouteRequest();
        Log.d(TAG, "Response from route url: " + routeStr);

        if (routeStr != null) {
            try {
                JSONObject jsonObj = new JSONObject(routeStr);

                // Getting JSON Array node
                JSONArray routes = jsonObj.getJSONArray("data");
                // loop through All routes
                for (int i = 0; i < routes.length(); i++) {
                    JSONObject route = routes.getJSONObject(i);
                    RouteList.addRouteList(
                            route.getString("route"),
                            route.getString("bound"),
                            route.getString("service_type"),
                            route.getString("orig_en"),
                            route.getString("orig_tc"),
                            route.getString("orig_sc"),
                            route.getString("dest_en"),
                            route.getString("dest_tc"),
                            route.getString("dest_sc")
                    );
                }
            } catch (final JSONException e) {
                Log.e(TAG, "Json parsing error: " + e.getMessage());
            }
        } else {
            Log.e(TAG, "Couldn't get json from server.");
        }
    }
    
    private void fetchStopList() {
        String stopStr = makeStopRequest();
        Log.d(TAG, "Response from stop url: " + stopStr);

        if (stopStr != null) {
            try {
                JSONObject jsonObj = new JSONObject(stopStr);

                // Getting JSON Array node
                JSONArray stops = jsonObj.getJSONArray("data");
                // loop through All routes
                for (int i = 0; i < stops.length(); i++) {
                    JSONObject stop = stops.getJSONObject(i);
                    StopList.addStopList(
                            stop.getString("stop"),
                            stop.getString("name_en"),
                            stop.getString("name_tc"),
                            stop.getString("name_sc"),
                            stop.getString("lat"),
                            stop.getString("long")
                    );
                }
            } catch (final JSONException e) {
                Log.e(TAG, "Json parsing error: " + e.getMessage());
            }
        } else {
            Log.e(TAG, "Couldn't get json from server.");
        }
    }
    
    private void fetchRouteStopList() {
        String routeStopStr = makeRouteStopRequest();
        Log.d(TAG, "Response from route-stop url: " + routeStopStr);

        if (routeStopStr != null) {
            try {
                JSONObject jsonObj = new JSONObject(routeStopStr);

                // Getting JSON Array node
                JSONArray routeStops = jsonObj.getJSONArray("data");
                // loop through All routes
                for (int i = 0; i < routeStops.length(); i++) {
                    JSONObject routeStop = routeStops.getJSONObject(i);
                    RouteStopList.addRouteStopList(
                            routeStop.getString("route"),
                            routeStop.getString("bound"),
                            routeStop.getString("service_type"),
                            routeStop.getString("seq"),
                            routeStop.getString("stop")
                    );
                }
            } catch (final JSONException e) {
                Log.e(TAG, "Json parsing error: " + e.getMessage());
            }
        } else {
            Log.e(TAG, "Couldn't get json from server.");
        }
    }

}
