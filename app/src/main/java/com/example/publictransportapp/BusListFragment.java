package com.example.publictransportapp;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.Manifest;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.publictransportapp.model.RouteEtaModel;
import com.example.publictransportapp.model.RouteListModel;
import com.example.publictransportapp.model.StopListModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class BusListFragment extends Fragment implements EtaCallback {

    private ArrayList<RouteEtaModel> busRecyclerList;
    private ArrayList<RouteListModel> routeList;
    private ArrayList<StopListModel> stopList;
    private RequestQueue requestQueue;
    private BusListAdapter busListAdapter;

    private FusedLocationProviderClient fusedLocationClient;
    private double userLat, userLong;

    private Handler handler;
    private Runnable runnable;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("Initialize", "Start onCreateView");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bus_list, container, false);

        // Set up recycler view
        RecyclerView busListView = view.findViewById(R.id.bus_list_recycler_view);

        busListView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        busRecyclerList = new ArrayList<>();
        busListAdapter = new BusListAdapter(busRecyclerList);
        busListView.setAdapter(busListAdapter);

        requestQueue = Volley.newRequestQueue(view.getContext());
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        // initialization
        routeList = new ArrayList<>();
        stopList = new ArrayList<>();

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(view.getContext());
        // set up location fetching interval
        final int INTERVAL_MILLIS = 60000;
        LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 100)
                .setIntervalMillis(INTERVAL_MILLIS)             // Sets the interval for location updates
                .setMinUpdateIntervalMillis(INTERVAL_MILLIS / 2)  // Sets the fastest allowed interval of location updates.
                .setWaitForAccurateLocation(false)              // Want Accurate location updates make it true or you get approximate updates
                .setMaxUpdateDelayMillis(100)                   // Sets the longest a location update may be delayed.
                .build();

        // Initialization for data refreshing
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                refreshData();
                handler.postDelayed(this, 60000);
            }
        };

        refreshData();
    }

    // refresh data
    private void refreshData() {
        Log.d("Initialize", "in refreshData");
        getAllRouteData();
        getAllStopData();
        setUpBusList();
        Log.d("Initialize", "finish refreshData");
    }

    // start data refresh
    @Override
    public void onResume() {
        super.onResume();
        handler.post(runnable);
    }

    // stop data refresh
    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
    }

    private void getAllStopData() {
        final String ALL_STOP_URL = "https://data.etabus.gov.hk/v1/transport/kmb/stop/";
        Log.d("Fetch Data", "in getAllStopData()");
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, ALL_STOP_URL, null,
                res -> {
                    Log.d("StopAPI Response", res.toString());
                    try {
                        JSONArray stops = res.getJSONArray("data");
                        for (int i = 0; i < stops.length(); i++) {
                            JSONObject stop = stops.getJSONObject(i);
                            stopList.add(new StopListModel(
                                    stop.getString("stop"),
                                    stop.getString("name_en"),
                                    stop.getString("name_tc"),
                                    stop.getString("name_sc"),
                                    stop.getDouble("lat"),
                                    stop.getDouble("long")));
                        }
                        Log.d("StopApi Data", stopList.toString());
                    } catch (JSONException e) {
                        Log.e("BusListFragment", e.toString());
                        if (getView() != null)
                            Toast.makeText(getView().getContext(), "Error parsing stop data", Toast.LENGTH_SHORT).show();
                    }
                    busListAdapter.notifyDataSetChanged();
                }, error -> {
                    Log.e("StopAPI Error", error.toString());
                    if (getView() != null)
                        Toast.makeText(getView().getContext(), "Error fetching stop data", Toast.LENGTH_SHORT).show();
                });
        Log.d("Fetch Data", "finish getAllStopData()");
        requestQueue.add(jsonObjectRequest);
    }

    private void getAllRouteData(){
        final String ALL_ROUTE_URL = "https://data.etabus.gov.hk/v1/transport/kmb/route/";
        Log.d("Fetch Data", "in getAllRouteData()");
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, ALL_ROUTE_URL, null,
                new Response.Listener<>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onResponse(JSONObject res) {
                        Log.d("RouteAPI Response", res.toString());
                        try {
                            JSONArray routes = res.getJSONArray("data");
                            for (int i = 0; i < routes.length(); i++) {
                                JSONObject route = routes.getJSONObject(i);
                                routeList.add(new RouteListModel(
                                        route.getString("route"),
                                        route.getString("bound"),
                                        route.getString("orig_en"),
                                        route.getString("orig_tc"),
                                        route.getString("orig_sc"),
                                        route.getString("dest_en"),
                                        route.getString("dest_tc"),
                                        route.getString("dest_sc"),
                                        route.getInt("service_type")));
                            }
                            Log.d("RouteApi Data", routeList.toString());
                        } catch (JSONException e) {
                            Log.e("BusListFragment", e.toString());
                            if (getView() != null)
                                Toast.makeText(getView().getContext(), "Error parsing route data", Toast.LENGTH_SHORT).show();
                        }
                        busListAdapter.notifyDataSetChanged();
                    }
                }, error -> {
                    Log.e("RouteAPI Error", error.toString());
                    if (getView() != null)
                        Toast.makeText(getView().getContext(), "Error fetching route data", Toast.LENGTH_SHORT).show();
                });
        Log.d("Fetch Data", "finish getAllRouteData()");
        //Log.d("RouteList", routeList.toString());
        //Log.d("RouteList Size", String.valueOf(routeList.size()));
        requestQueue.add(jsonObjectRequest);
    }

    private void getUserLocation() {
        assert getActivity() != null;
        // check for permission on location access
        if (getView() != null) {
            if (ActivityCompat.checkSelfPermission(getView().getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(getView().getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            }
            // get location if permission granted
            if (ActivityCompat.checkSelfPermission(getView().getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(getView().getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                fusedLocationClient.getLastLocation()
                        .addOnSuccessListener(getActivity(), location -> {
                            if (location != null) {
                                userLat = location.getLatitude();
                                userLong = location.getLongitude();
                            }
                        });
            }
        }
        Log.d("Fetch User Location", "user_lat: " + userLat + "user_long" + userLong);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void setUpBusList() {
        // search for 200m radius of route if have permission
        if (getView() != null) {
            if (ActivityCompat.checkSelfPermission(getView().getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(getView().getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                getUserLocation();
                for (int i = 0; i < stopList.size(); i++) {
                    if (calculateDistance(stopList.get(i).getLat(), stopList.get(i).getLon()) > 0.2) {
                        addBusRow(i);
                    }
                }
            } else { // display all route in ascending with origin as stop
                for (int i = 0; i < routeList.size(); i++) {
                    addBusRow(i);
                }
            }
        }
        busListAdapter.notifyDataSetChanged();
        Log.d("Display Bus List", busRecyclerList.toString());
    }
    private void addBusRow(int i) {
        String stop = routeList.get(i).getOrig_en();
        String dest = routeList.get(i).getDest_en();
        String route = routeList.get(i).getRoute();
        int serviceType = routeList.get(i).getServiceType();
        String direction = routeList.get(i).getDirection();
        String stopId = stopList.get(i).getStopId();

        if (stopId != null) {
            EtaRetriever etaRetriever = new EtaRetriever(requestQueue);
            etaRetriever.getEta(stopId, route, serviceType, dest, direction, new EtaCallback() {
                @Override
                public void onEtaReceived(RouteEtaModel routeEtaModel) {
                    Log.d("BusListFragment", "ETA received: " + routeEtaModel.toString());
                    busRecyclerList.add(new RouteEtaModel(
                            route,
                            direction,
                            serviceType,
                            stop,
                            dest,
                            routeEtaModel.getEta1()
                    ));
                    busListAdapter.notifyDataSetChanged(); // Notify adapter after adding
                    Log.d("BusListFragment", "ETA notified");
                }

                @Override
                public void onError(String error) {
                    Log.e("BusListFragment", "Error: " + error);
                }
            });
        } else {
            Log.e("BusListFragment", "Stop ID not found for stop: " + stop);
        }
    }

    private double calculateDistance(double lat, double lon) {
        double userLatR = Math.toRadians(userLat),
                userLongR = Math.toRadians(userLong),
                latR = Math.toRadians(lat),
                lonR = Math.toRadians(lon);
        return Math.acos((Math.sin(userLatR) * Math.sin(latR)
                + Math.cos(userLongR) * Math.cos(lonR) * Math.cos(userLongR - lonR))) * 6371;
    }

    @Override
    public void onEtaReceived(RouteEtaModel routeEtaModel) {
        Log.d("BusListFragment", "ETA received: " + routeEtaModel.toString());

    }

    @Override
    public void onError(String error) {
        Log.d("BusListFragment", "Error");
    }
}