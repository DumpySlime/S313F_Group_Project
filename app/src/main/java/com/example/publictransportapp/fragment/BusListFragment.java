package com.example.publictransportapp.fragment;

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
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.example.publictransportapp.BusRowAdapter;
import com.example.publictransportapp.EtaHandlerThread;
import com.example.publictransportapp.R;
import com.example.publictransportapp.model.BusRowList;
import com.example.publictransportapp.model.ETAList;
import com.example.publictransportapp.model.RouteRowList;
import com.example.publictransportapp.model.RouteStopList;
import com.example.publictransportapp.model.StopList;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class BusListFragment extends Fragment {

    private final String TAG = "BusListFragment";
    private ListView busListView;
    private FusedLocationProviderClient fusedLocationClient;
    private double userLat, userLong;
    private Handler handler;
    private Runnable etaRunnable;
    private BusRowAdapter busRowAdapter;
    private double detectionRange;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Log.d("BusListFragment", "Initialize onCreateView");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bus_list, container, false);

        busListView = view.findViewById(R.id.bus_list_view);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView titleBar = getActivity().findViewById(R.id.title_bar);
        titleBar.setText(R.string.busList_button);

        ImageButton filterButton = getActivity().findViewById(R.id.filter_button);
        filterButton.setVisibility(View.VISIBLE);
        // check if user location is granted
        getUserLocation();

        // get applied range
        if (getArguments() != null) {
            detectionRange = getArguments().getDouble("detection_range", 200);
        }

        // Populate busRowList
        handler = new Handler();
        etaRunnable = new Runnable() {
            @Override
            public void run() {
                fetchBusData();
                handler.postDelayed(this, 60000);
            }
        };

        Log.d(TAG, "BusRowList: " + BusRowList.busRowList.toString());

        // Create Adapter for busListView
        busRowAdapter = new BusRowAdapter(
                this.getContext(),
                BusRowList.busRowList
        );
        busListView.setAdapter(busRowAdapter);

        busListView.setOnItemClickListener(
                (adapterView, view1, i, l) -> {
                    // Pass route, service_type and direction as arguements
                    HashMap<String, String> busRow = BusRowList.busRowList.get(i);
                    Bundle bundle = new Bundle();
                    bundle.putString("route", busRow.get("route"));
                    bundle.putString("service_type", busRow.get("service_type"));
                    bundle.putString("direction", busRow.get("direction"));

                    // Create a new instance of RouteEtaFragment
                    RouteEtaFragment routeEtaFragment = new RouteEtaFragment();
                    routeEtaFragment.setArguments(bundle);

                    // Replace the current fragment with RouteEtaFragment
                    if (getActivity() != null) {
                        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.fragment_container, routeEtaFragment)
                                .addToBackStack(null)
                                .commit();
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
    }


    // start data refresh
    @Override
    public void onResume() {
        super.onResume();
        fetchBusData();
        handler.post(etaRunnable);
    }

    // stop data refresh
    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(etaRunnable);
    }


    private void fetchBusData() {
        RouteRowList.clearRouteRowList();
        RouteStopList.clearRouteStopList();
        ETAList.clearETAList();
        BusRowList.clearBusList();
        // search for bus id that is <0.5km
        //Log.d(TAG, "Fetching Data for BusRowList...");
        for (int i = 0; i < StopList.stopList.size(); i++) {
            if (StopList.stopList.get(i) != null) {
                double stopLat =  Double.parseDouble(StopList.stopList.get(i).get("lat"));
                double stopLong = Double.parseDouble(StopList.stopList.get(i).get("long"));
                //Log.d(TAG, "Determining range of Object: " + StopList.stopList.get(i));
                if (calculateDistance(stopLat, stopLong) < detectionRange / 1000) {
                    // get stopId and stopName for BusRowList
                    String stopId = StopList.stopList.get(i).get("stopId");
                    //Log.d(TAG, "StopId: " + stopId);
                    String stopName = StopList.stopList.get(i).get("name_en");
                    //Log.d(TAG, "Stop Name: " + stopName);

                    // get from stop-eta
                    EtaHandlerThread etaHandlerThread = new EtaHandlerThread("stop-eta/" + stopId);
                    etaHandlerThread.start();

                    try {
                        etaHandlerThread.join();
                    } catch (InterruptedException e) {
                        Log.e(TAG, e.getMessage());
                    }

                    // get serviceType, route, dest, direction and eta for BusRowList
                    for (int j = 0; j < ETAList.etaList.size(); j++) {
                        // only get data for eta_seq 1
                        if (ETAList.etaList.get(j).get("eta_seq").equals("1")) {
                            String serviceType = ETAList.etaList.get(j).get("serviceType");
                            //Log.d(TAG, "Service Type: " + serviceType);
                            String route = ETAList.etaList.get(j).get("route");
                            //Log.d(TAG, "Route: " + route);
                            String dest = ETAList.etaList.get(j).get("dest_en");
                            //Log.d(TAG, "Destination: " + dest);
                            String direction = ETAList.etaList.get(j).get("direction");
                            //Log.d(TAG, "Direction: " + direction);
                            // calculate Eta
                            String fetchedEta = ETAList.etaList.get(j).get("eta");
                            String eta = calculateEta(fetchedEta);
                            //Log.d(TAG, "Eta_seq: " + ETAList.etaList.get(j).get("eta_seq"));


                            // save data to BusRowList
                            BusRowList.addBusList(
                                    route,
                                    stopName,
                                    dest,
                                    eta,
                                    serviceType,
                                    direction,
                                    stopId,
                                    Double.toString(stopLat),
                                    Double.toString(stopLong)
                            );
                        }
                    }
                }
            }
        }
        //Log.d(TAG, "busrowList in fetchBusData: " + BusRowList.busRowList.toString());
        Comparator com = new Comparator<HashMap<String, String>>() {
            @Override
            public int compare(HashMap<String, String> o, HashMap<String, String> t1) {
                if ((o.get("lat") != null) && (o.get("lon") != null) && (t1.get("lat") != null) && (t1.get("lon") != null)) {
                    if (calculateDistance(Double.parseDouble(o.get("lat")),Double.parseDouble(o.get("lon"))) > calculateDistance(Double.parseDouble(t1.get("lat")),Double.parseDouble(t1.get("lon")))){
                        return 1;
                    } else {
                        return -1;
                    }
                }
                return 0;
            }
        };
        Collections.sort(BusRowList.busRowList, com);
        busRowAdapter.notifyDataSetChanged();
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
                Log.d(TAG, "user_lat: " + userLat + "; user_long: " + userLong);
            } else { // set Default location to HKMU
                userLat = 22.31624228793303;
                userLong = 114.18048655839682;
                Log.d(TAG, "Use Default location");
            }
        }
    }

    private String calculateEta(String fetchedEta) {
        String eta = "NA";
        if ((fetchedEta != null) && ((!fetchedEta.equals("null")))) {
            OffsetDateTime offsetDateTime = OffsetDateTime.parse(fetchedEta);
            LocalDateTime etaDateTime = offsetDateTime.atZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
            int minutesUntilArrival = (int) ChronoUnit.MINUTES.between(LocalDateTime.now(), etaDateTime);
            if (minutesUntilArrival <= 0) {
                eta = "Arriving";
            } else {
                eta =  minutesUntilArrival + "min";
            }
        }
        return eta;
    }
    private double calculateDistance(double lat, double lon) {
        final int R = 6371; // Radius of the Earth in kilometers
        // Convert degrees to radians
        double lat1Rad = Math.toRadians(userLat);
        double lon1Rad = Math.toRadians(userLong);
        double lat2Rad = Math.toRadians(lat);
        double lon2Rad = Math.toRadians(lon);

        // Differences
        double deltaLat = lat2Rad - lat1Rad;
        double deltaLon = lon2Rad - lon1Rad;

        // Haversine formula
        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
                Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                        Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        // Calculate the distance in kilometers
        // Log.d(TAG, "Distance: " + distanceInKm);
        return R * c;
    }
}