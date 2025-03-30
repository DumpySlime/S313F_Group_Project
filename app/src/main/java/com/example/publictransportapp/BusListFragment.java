package com.example.publictransportapp;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.Manifest;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.publictransportapp.model.BusRowList;
import com.example.publictransportapp.model.BusETAList;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

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
import java.util.HashMap;

public class BusListFragment extends Fragment {

    private ListView busListView;
    private FusedLocationProviderClient fusedLocationClient;
    private double userLat, userLong;
    private Handler handler;
    private Runnable etaRunnable;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("BusListFragment", "Initialize onCreateView");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bus_list, container, false);

        busListView = (ListView) view.findViewById(R.id.bus_list_view);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Create Adapter for busListView
        BusRowAdapter adapter = new BusRowAdapter(
                this.getContext(),
                BusRowList.busRowList
        );
        busListView.setAdapter(adapter);

        busListView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        // Pass route, service_type and direction as arguements
                        HashMap<String, String> busRow = BusRowList.busRowList.get(i);
                        Bundle bundle = new Bundle();
                        bundle.putString("ROUTE", busRow.get("ROUTE"));
                        bundle.putString("SERVICE_TYPE", busRow.get("SERVICE_TYPE"));
                        bundle.putString("DIRECTION", busRow.get("DIRECTION"));

                        // Create a new instance of RouteEtaFragment
                        RouteEtaFragment routeEtaFragment = new RouteEtaFragment();

                        // Replace the current fragment with RouteEtaFragment
                        if (getActivity() != null) {
                            FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                            fragmentTransaction.replace(R.id.fragment_container, routeEtaFragment)
                                    .addToBackStack(null)
                                    .commit();
                        }

                    }
                }
        );

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
        etaRunnable = new Runnable() {
            @Override
            public void run() {
                EtaHandlerThread etaHandlerThread = new EtaHandlerThread("A60AE774B09A5E44", "40", "1");
                etaHandlerThread.start();
                handler.postDelayed(this, 60000);
            }
        };
    }

    // start data refresh
    @Override
    public void onResume() {
        super.onResume();
        handler.post(etaRunnable);
    }

    // stop data refresh
    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(etaRunnable);
    }
    private boolean getUserLocation() {
        assert getActivity() != null;
        boolean permission = false;
        // check for permission on location access
        if (getView() != null) {
            if (ActivityCompat.checkSelfPermission(getView().getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(getView().getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
                Log.d("BusListFragment", "Fetch location permission");
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
                permission = true;
                Log.d("Fetch User Location", "user_lat: " + userLat + "; user_long" + userLong);
            }
        }
        return permission;
    }
/*
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
                new FetchEtaTask(routeStopList, false).execute();
            }
        }
        busListAdapter.updateData(busRecyclerList);
        //Log.d("Display Bus List", busRecyclerList.toString());
    }
*/
    private double calculateDistance(double lat, double lon) {
        double userLatR = Math.toRadians(userLat),
                userLongR = Math.toRadians(userLong),
                latR = Math.toRadians(lat),
                lonR = Math.toRadians(lon);
        return Math.acos((Math.sin(userLatR) * Math.sin(latR)
                + Math.cos(userLongR) * Math.cos(lonR) * Math.cos(userLongR - lonR))) * 6371;
    }

    private static class EtaHandlerThread extends Thread {
        private static final String TAG = "EtaHandlerThread";
        private static String baseEtaUrl = "https://data.etabus.gov.hk/v1/transport/kmb/eta/";

        private static String stopId, route, serviceType;

        EtaHandlerThread(String stopId, String route, String serviceType) {
            EtaHandlerThread.stopId = stopId;
            EtaHandlerThread.route = route;
            EtaHandlerThread.serviceType = serviceType;
        }

        public static String makeEtaRequest() {
            String response = null;

            try {
                URL url = new URL(baseEtaUrl + stopId
                        + "/" + route
                        + "/" + serviceType);
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
            Log.d(TAG, "Response from route url: " + etaStr);

            if (etaStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(etaStr);

                    // Getting JSON Array node
                    JSONArray etas = jsonObj.getJSONArray("data");
                    // reset etaList
                    BusETAList.clearETAList();
                    // loop through All routes
                    for (int i = 0; i < etas.length(); i++) {
                        JSONObject eta = etas.getJSONObject(i);
                        BusETAList.addBusETAList(
                                eta.getString("co"),
                                eta.getString("route"),
                                eta.getString("direction"),
                                eta.getString("serviceType"),
                                eta.getString("seq"),
                                eta.getString("dest_en"),
                                eta.getString("dest_tc"),
                                eta.getString("dest_sc"),
                                eta.getString("eta_seq")
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
}