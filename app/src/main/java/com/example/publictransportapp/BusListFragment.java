package com.example.publictransportapp;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
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
import com.example.publictransportapp.model.RouteSearchModel;
import com.example.publictransportapp.model.RouteStopListModel;
import com.example.publictransportapp.model.StopListModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class BusListFragment extends Fragment implements EtaCallback {

    private ArrayList<RouteEtaModel> busRecyclerList;
    private ArrayList<RouteListModel> routeList;
    private ArrayList<StopListModel> stopList;
    private ArrayList<RouteStopListModel> routeStopList;
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
        busListAdapter = new BusListAdapter(busRecyclerList, stopList);
        busListView.setAdapter(busListAdapter);

        requestQueue = Volley.newRequestQueue(view.getContext());
        // initialization
        routeList = new ArrayList<>();
        stopList = new ArrayList<>();
        routeStopList = new ArrayList<>();

        getInitialData();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(view.getContext());
        // set up location fetching interval
        final int INTERVAL_MILLIS = 120000;
        LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 100)
                .setIntervalMillis(INTERVAL_MILLIS)             // Sets the interval for location updates
                .setMinUpdateIntervalMillis(INTERVAL_MILLIS / 2)  // Sets the fastest allowed interval of location updates.
                .setWaitForAccurateLocation(false)              // Want Accurate location updates make it true or you get approximate updates
                .setMaxUpdateDelayMillis(100)                   // Sets the longest a location update may be delayed.
                .build();

        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                setUpBusList();
                handler.postDelayed(this, INTERVAL_MILLIS);
            }
        };
    }

    private void getInitialData() {
        final int[] counter = {0};
        getAllRouteData(() -> {
            counter[0]++;
            if (counter[0] == 3) {
                Log.d("BusListFragment", "Finish getting All Data");
                setUpBusList();
            }
        });
        getAllStopData(() -> {
            counter[0]++;
            if (counter[0] == 2) {
                Log.d("BusListFragment", "Finish getting All Data");
                setUpBusList();
            }
        });
        getAllRouteStopData(() -> {
            counter[0]++;
            if (counter[0] == 2) {
                Log.d("BusListFragment", "Finish getting All Data");
                setUpBusList();
            }
        });
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

    private void getAllStopData(DataFetchCallback callback) {
        final String ALL_STOP_URL = "https://data.etabus.gov.hk/v1/transport/kmb/stop/";
        //Log.d("Fetch Data", "in getAllStopData()");
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, ALL_STOP_URL, null,
                res -> {
                    //Log.d("StopAPI Response", res.toString());
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
                        if (callback != null) {
                            callback.onDataFetched();
                        }
                        //Log.d("StopApi Data", stopList.toString());
                    } catch (JSONException e) {
                        Log.e("BusListFragment", e.toString());
                        if (getView() != null)
                            Toast.makeText(getView().getContext(), "Error parsing stop data", Toast.LENGTH_SHORT).show();
                    }
                }, error -> {
            Log.e("StopAPI Error", error.toString());
            if (getView() != null)
                Toast.makeText(getView().getContext(), "Error fetching stop data", Toast.LENGTH_SHORT).show();
        });
        // Log.d("Fetch Data", "finish getAllStopData()");
        requestQueue.add(jsonObjectRequest);
    }

    private void getAllRouteData(DataFetchCallback callback) {
        final String ALL_ROUTE_URL = "https://data.etabus.gov.hk/v1/transport/kmb/route/";
        // Log.d("Fetch Data", "in getAllRouteData()");
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, ALL_ROUTE_URL, null,
                new Response.Listener<>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onResponse(JSONObject res) {
                        // Log.d("RouteAPI Response", res.toString());
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
                            if (callback != null) {
                                callback.onDataFetched(); // Notify that route data is fetched
                            }
                            //Log.d("RouteApi Data", routeList.toString());
                        } catch (JSONException e) {
                            Log.e("BusListFragment", e.toString());
                            if (getView() != null)
                                Toast.makeText(getView().getContext(), "Error parsing route data", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, error -> {
            Log.e("RouteAPI Error", error.toString());
            if (getView() != null)
                Toast.makeText(getView().getContext(), "Error fetching route data", Toast.LENGTH_SHORT).show();
        });
        //Log.d("Fetch Data", "finish getAllRouteData()");
        //Log.d("RouteList", routeList.toString());
        //Log.d("RouteList Size", String.valueOf(routeList.size()));
        requestQueue.add(jsonObjectRequest);
    }

    private void getAllRouteStopData(DataFetchCallback callback) {
        final String ALL_ROUTE_STOP_URL = "https://data.etabus.gov.hk/v1/transport/kmb/route-stop/";
        // Log.d("Fetch Data", "in getAllRouteData()");
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, ALL_ROUTE_STOP_URL, null,
                new Response.Listener<>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onResponse(JSONObject res) {
                        // Log.d("RouteAPI Response", res.toString());
                        try {
                            JSONArray routes = res.getJSONArray("data");
                            for (int i = 0; i < routes.length(); i++) {
                                JSONObject route = routes.getJSONObject(i);
                                routeStopList.add(new RouteStopListModel(
                                        route.getString("route"),
                                        route.getString("bound"),
                                        route.getInt("service_type"),
                                        route.getString("seq"),
                                        route.getString("stop")
                                        ));
                            }
                            if (callback != null) {
                                callback.onDataFetched(); // Notify that route data is fetched
                            }
                            //Log.d("RouteApi Data", routeStopList.toString());
                        } catch (JSONException e) {
                            Log.e("BusListFragment", e.toString());
                            if (getView() != null)
                                Toast.makeText(getView().getContext(), "Error parsing route-stop data", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, error -> {
            Log.e("RouteStopAPI Error", error.toString());
            if (getView() != null)
                Toast.makeText(getView().getContext(), "Error fetching route-stop data", Toast.LENGTH_SHORT).show();
        });
        //Log.d("Fetch Data", "finish getAllRouteStopData()");
        //Log.d("RouteStopList", routeStopList.toString());
        //Log.d("RouteStopList Size", String.valueOf(routeStopList.size()));
        requestQueue.add(jsonObjectRequest);
    }

    private void getUserLocation() {
        assert getActivity() != null;
        // check for permission on location access
        if (getView() != null) {
            Log.d("in BusListFragment", "getView() not null");
            if (ActivityCompat.checkSelfPermission(getView().getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(getView().getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
                Log.d("in BusListFragment", "Fetch location permission");
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
                Log.d("Fetch User Location", "user_lat: " + userLat + "user_long" + userLong);
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void setUpBusList() {
        // search for 200m radius of route if have permission
        if (getView() != null) {
            if (ActivityCompat.checkSelfPermission(getView().getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(getView().getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Log.d("in BusListFragment", "get route within 200m");
                getUserLocation();
                new FetchEtaTask(routeStopList, true).execute();
            } else { // display all route in ascending with origin as stop
                Log.d("in BusListFragment", "get all route| Size: " + routeList.size());
                new FetchEtaTask(routeList, false).execute();
            }
        }
        busListAdapter.updateData(busRecyclerList);
        //Log.d("Display Bus List", busRecyclerList.toString());
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

    private class FetchEtaTask extends AsyncTask<Void, Void, List<RouteEtaModel>> {

        private List<RouteSearchModel> displayedRoutes;
        private boolean useUserLocation;

        private String stop, dest, route, direction;
        private int serviceType;
        private StopListModel stopObject;

        public FetchEtaTask(List<RouteSearchModel> displayedRoutes, boolean useUserLocation) {
            this.displayedRoutes = displayedRoutes;
            this.useUserLocation = useUserLocation;
            this.stopObject = null;
        }

        @Override
        protected List<RouteEtaModel> doInBackground(Void... voids) {
            List<RouteEtaModel> etas = new ArrayList<>();
            Log.d("BusListFragment.FetchEtaTask", "Bus Row List: " + displayedRoutes.toString());
            for (RouteStopListModel routes : displayedRoutes) {
                boolean haveStopObject = false;
                // find stop object
                for (StopListModel stopListModel : stopList) {
                    if (stopListModel.getStopId().equals(routes.getStopId())) {
                        haveStopObject = true;
                        stopObject = stopListModel;
                        break;
                    }
                }
                stop = routes.getOrig_en();
                dest = routes.getDest_en();
                route = routes.getRoute();
                serviceType = routes.getServiceType();
                direction = routes.getDirection();
                // search stopId
                Log.d("BusListFragment.FetchEtaTask", "Target stop name: " + stop);
                for (StopListModel stops : stopList) {
                    //Log.d("BusListFragment.FetchEtaTask", "Route Orig: " + stop + ", Stop Name: " + stops.getName_en());
                    if (stops.getName_en().equals(stop)) {
                        stopObject = stops;
                        haveStopObject = true;
                        break;
                    }
                }
                Log.d("BusListFragment.FetchEtaTask", "Result: " + stopObject.getStopId());

                if (haveStopObject) {
                    Log.d("BusListFragment.FetchEtaTack", "Stop Object found: " + stopObject.toString());
                    if (useUserLocation) {
                        if (calculateDistance(stopObject.getLat(), stopObject.getLon()) > 0.2) {
                            fetchEtaForRoute(etas, stopObject.getStopId(), route, serviceType, dest, direction);
                        }
                    } else {
                        fetchEtaForRoute(etas, stopObject.getStopId(), route, serviceType, dest, direction);
                    }
                } else {
                    Log.e("BusListFragment.FetchEtaTack", "Stop id not found");
                }
            }
            return etas;
        }

        private void fetchEtaForRoute(List<RouteEtaModel> etas, String stopId, String route, int serviceType, String dest, String direction) {
            EtaRetriever etaRetriever = new EtaRetriever(requestQueue);
            CountDownLatch latch = new CountDownLatch(1);

            etaRetriever.getEta(stopId, route, serviceType, dest, direction, new EtaCallback() {
                @Override
                public void onEtaReceived(RouteEtaModel routeEtaModel) {
                    // Log.d("BusListFragment", "ETA received: " + routeEtaModel.toString());
                    boolean exists = false;
                    for (RouteEtaModel model : busRecyclerList) {
                        if (model.getRoute().equals(route) && model.getDirection().equals(direction) && model.getStopId().equals(stopId) && (model.getServiceType() == serviceType)) {
                            exists = true;
                            break;
                        }
                    }
                    if (!exists) {
                        busRecyclerList.add(new RouteEtaModel(
                                route,
                                direction,
                                serviceType,
                                stopId,
                                dest,
                                routeEtaModel.getEta1()
                        ));
                        Log.d("BusListFragment", "added eta{ " +
                                "\nroute: " + route +
                                ";\ndirection: " + direction +
                                ";\nserviceType: " + serviceType +
                                ";\nstop: " + stopId +
                                ";\ndestination: " + dest +
                                ";\neta: " + routeEtaModel.getEta1() + "}");
                    }
                    latch.countDown();
                    // Log.d("BusListFragment", "ETA notified");
                }

                @Override
                public void onError(String error) {
                    Log.e("FetchEtaTask", "Error fetching ETA: " + error);
                    latch.countDown();
                }
            });

            try {
                latch.await();
            } catch (InterruptedException e) {
                Log.e("FetchEtaTask", "Interrupted while waiting for ETA: " + e.getMessage());
            }
        }

        @Override
        protected void onPostExecute(List<RouteEtaModel> etas) {
            busListAdapter.updateData(etas);
        }
    }
}