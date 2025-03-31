package com.example.publictransportapp;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;

public class ApiClient {
    private static final String TAG = "ApiClient";
    private static final OkHttpClient client = new OkHttpClient();
    private static final String BASE_URL = "https://data.etabus.gov.hk/v1/transport/kmb/";

    public static String getRouteInfo(String route, String direction, String serviceType) throws IOException {
        String url = BASE_URL + "route/" + route + "/" + direction + "/" + serviceType;
        return executeRequest(url);
    }

    private static String executeRequest(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .header("Accept", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Request Failed: " + response.code() + " " + response.message());
            }
            return response.body().string();
        }
    }
}
