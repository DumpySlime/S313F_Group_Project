package com.example.publictransportapp.api;


import com.example.publictransportapp.model.RouteStopResponse;
import com.example.publictransportapp.model.StopResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface KmbApi {
    @Headers({
            "X-API-KEY: YOUR_API_KEY_HERE"
    })
    @GET("v1/transport/kmb/stop")
    Call<StopResponse> getAllStops();

    @Headers({
            "X-API-KEY: YOUR_API_KEY_HERE"
    })
    @GET("v1/transport/kmb/route-stop")
    Call<RouteStopResponse> getAllRouteStops();

    // 带有额外参数的备用方法
    @GET("v1/transport/kmb/stop")
    Call<StopResponse> getAllStopsWithFormat(@Query("format") String format);

    @GET("v1/transport/kmb/route-stop")
    Call<RouteStopResponse> getAllRouteStopsWithFormat(@Query("format") String format);
}