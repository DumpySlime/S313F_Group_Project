package com.example.publictransportapp;

import android.util.Log;

import com.example.publictransportapp.model.RouteStopList;

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

public class RouteStopHandlerThread extends Thread {
    private static final String TAG = "RouteStopHandlerThread";
    private final String baseRouteStopUrl = "https://data.etabus.gov.hk/v1/transport/kmb/route-stop/";

    private static String route, direction, serviceType;

    public RouteStopHandlerThread (String route, String direction, String serviceType) {
        RouteStopHandlerThread.route = route;
        if (direction.equals("O")) {    // change arguements to match query
            RouteStopHandlerThread.direction = "outbound";
        } else {
            RouteStopHandlerThread.direction = "inbound";
        }
        RouteStopHandlerThread.serviceType = serviceType;
    }

    @Override
    public void run() {

        String routeStopStr = makeRouteStopRequest();
        Log.d(TAG, "route-stop url: " + baseRouteStopUrl + route + "/" + direction + "/" + serviceType);
        Log.d(TAG, "Response from route-stop url: " + routeStopStr);

        if (routeStopStr != null) {
            try {
                JSONObject jsonObj = new JSONObject(routeStopStr + route + "/" + direction + "/" + serviceType);

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

    public String makeRouteStopRequest() {
        String response = null;

        try {
            URL url = new URL(baseRouteStopUrl);
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

}
