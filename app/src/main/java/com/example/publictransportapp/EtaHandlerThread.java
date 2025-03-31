package com.example.publictransportapp;


import android.util.Log;

import com.example.publictransportapp.model.ETAList;

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

public class EtaHandlerThread extends Thread {
    private static final String TAG = "StopEtaHandlerThread";
    private static String baseEtaUrl = "https://data.etabus.gov.hk/v1/transport/kmb/";

    private static String query;

    EtaHandlerThread(String query) {
        EtaHandlerThread.query = query;
    }

    public static String makeEtaRequest() {
        String response = null;

        try {
            URL url = new URL(baseEtaUrl + query);
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
        String etaStr = makeEtaRequest();
        Log.d(TAG, "Eta url:" + baseEtaUrl + query);
        Log.d(TAG, "Response from Eta url: " + etaStr);

        if (etaStr != null) {
            try {
                JSONObject jsonObj = new JSONObject(etaStr);

                // Getting JSON Array node
                JSONArray etas = jsonObj.getJSONArray("data");
                if (etas.length() > 0) {
                    // loop through All routes
                    for (int i = 0; i < etas.length(); i++) {
                        JSONObject eta = etas.getJSONObject(i);
                        ETAList.addETAList(
                                eta.getString("co"),
                                eta.getString("route"),
                                eta.getString("dir"),
                                eta.getString("service_type"),
                                eta.getString("seq"),
                                eta.getString("dest_en"),
                                eta.getString("dest_tc"),
                                eta.getString("dest_sc"),
                                eta.getString("eta_seq"),
                                eta.getString("eta")
                        );
                    }
                    Log.d(TAG, "Saved ETAList: " + ETAList.etaList.toString());

                }

            } catch (final JSONException e) {
                Log.e(TAG, "Json parsing error: " + e.getMessage());
            }
        } else {
            Log.e(TAG, "Couldn't get json from server.");
        }
    }
}