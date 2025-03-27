package com.example.publictransportapp;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.Manifest;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.publictransportapp.model.BusRowModel;
import com.example.publictransportapp.model.RouteListModel;
import com.example.publictransportapp.model.StopListModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class BusListFragment extends Fragment implements EtaRetriever{

    private ArrayList<BusRowModel> busRecyclerList;
    private ArrayList<RouteListModel> routeList;
    private ArrayList<StopListModel> stopList;
    private RecyclerView busListView;
    private BusListAdapter busListAdapter;
    private RequestQueue requestQueue;

    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;
    private double userLat, userLong;
    private final int INTERVAL_MILLIS = 60000;

    private final String ALL_STOP_URL = "https://data.etabus.gov.hk/v1/transport/kmb/stop",
        ALL_ROUTE_URL = "https://data.etabus.gov.hk/v1/transport/kmb/route";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bus_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        // initalization
        routeList = new ArrayList<>();
        stopList = new ArrayList<>();
        busRecyclerList = new ArrayList<>();
        busListAdapter = new BusListAdapter(busRecyclerList);
        busListView = view.findViewById(R.id.bus_list_recycler_view);


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(view.getContext());
        // set up location fetching interval
        locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 100)
                .setIntervalMillis(INTERVAL_MILLIS)             // Sets the interval for location updates
                .setMinUpdateIntervalMillis(INTERVAL_MILLIS/2)  // Sets the fastest allowed interval of location updates.
                .setWaitForAccurateLocation(false)              // Want Accurate location updates make it true or you get approximate updates
                .setMaxUpdateDelayMillis(100)                   // Sets the longest a location update may be delayed.
                .build();
        getAllRouteData();
        getAllStopData();

        setUpBusList(view);
    }


    //refresh data
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser){
        super.setUserVisibleHint(isVisibleToUser);
        if (getFragmentManager() != null) {
            getFragmentManager()
                    .beginTransaction()
                    .detach(this)
                    .attach(this)
                    .commit();
        }
    }

    private void getAllStopData() {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, ALL_STOP_URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject res) {
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
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(), "Error parsing stop data", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "Error fetching stop data", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(jsonObjectRequest);
    }
    private void getAllRouteData(){
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, ALL_ROUTE_URL, null,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject res) {
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
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Error parsing route data", Toast.LENGTH_SHORT).show();
                    }
                }
            }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "Error fetching route data", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(jsonObjectRequest);
    }

    private void getUserLocation() {
        // check for permission on location access
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }
        // get location if permission granted
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                userLat = location.getLatitude();
                                userLong = location.getLongitude();
                            }
                        }
                    });
        }
    }

    private void setUpBusList(View view) {
        // search for 200m radius of route if have permission
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            getUserLocation();
            for (int i = 0; i < stopList.size(); i++) {
                if (calculateDistance(stopList.get(i).getLat(), stopList.get(i).getLon()) > 0.2) {
                    addBusRow(i, view);
                }
            }
        } else { // display all route in ascending with origin as stop
            for (int i = 0; i < routeList.size(); i++) {
                addBusRow(i, view);
            }
        }
    }

    private void addBusRow(int i, View view) {
        String stop = routeList.get(i).getOrig_en();
        String dest = routeList.get(i).getDest_en();
        String route = routeList.get(i).getRoute();
        int serviceType = routeList.get(i).getService_type();
        busRecyclerList.add(new BusRowModel(stop, dest, route, EtaRetriever.getRouteStopETA(getStopId(stop), route, serviceType, view, requestQueue).getEta(0)));
    }

    private String getStopId(String stop) {
        String stopId = null;
        while (stopId == null) {
            int i = 0;
            if (stopList.get(i).getName_en() == stop) {
                stopId = stopList.get(i).getStopId();
            }
        }
        return stopId;
    }

    private double calculateDistance(double lat, double lon) {
        double userLatR = Math.toRadians(userLat),
                userLongR = Math.toRadians(userLong),
                latR = Math.toRadians(lat),
                lonR = Math.toRadians(lon);
        return Math.acos((Math.sin(userLatR) * Math.sin(latR)
                + Math.cos(userLongR) * Math.cos(lonR) * Math.cos(userLongR - lonR))) * 6371;
    }
}