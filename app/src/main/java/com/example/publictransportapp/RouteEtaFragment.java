package com.example.publictransportapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.publictransportapp.model.ETAList;
import com.example.publictransportapp.model.RouteRowList;
import com.example.publictransportapp.model.RouteStopList;
import com.example.publictransportapp.model.StopList;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;

public class RouteEtaFragment extends Fragment {

    private final String TAG = "RouteEtaFragment";
    private ListView routeListView;
    private RouteListAdapter routeListAdapter;
    private String route, serviceType, direction;
    private Handler handler;
    private Runnable etaRunnable;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_route_eta, container, false);

        routeListView = (ListView) view.findViewById(R.id.route_eta_list_view);

        route = null;
        serviceType = null;
        direction = null;

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        // get relevant data from pressing
        Bundle args = getArguments();
        if (args != null) {
            route = args.getString("route");
            serviceType = args.getString("service_type");
            direction = args.getString("direction");
        }

        // Populate routeEtaList
        handler = new Handler();
        etaRunnable = new Runnable() {
            @Override
            public void run() {
                fetchRouteData();
                handler.postDelayed(this, 60000);
            }
        };

        Log.d(TAG, "RouteRowList: " + RouteRowList.routeRowList.toString());

        // Create Adapter for RouteEtaList
        routeListAdapter = new RouteListAdapter(
                this.getContext(),
                RouteRowList.routeRowList
        );
        routeListView.setAdapter(routeListAdapter);
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

    private void fetchRouteData() {
        // clear previous data
        RouteRowList.clearRouteRowList();
        ETAList.clearETAList();
        RouteStopList.clearRouteStopList();
        // get from route-eta
        EtaHandlerThread etaHandlerThread = new EtaHandlerThread("route-eta/" + route + "/" + serviceType);
        etaHandlerThread.start();

        try {
            etaHandlerThread.join();
        } catch (InterruptedException e) {
            Log.e(TAG, e.getMessage());
        }

        // fetch relevant stop name
        // fetch stop id of that route, direction, service type
        RouteStopHandlerThread routeStopHandlerThread = new RouteStopHandlerThread(route, direction, serviceType);
        routeStopHandlerThread.start();

        try {
            routeStopHandlerThread.join();
        } catch (InterruptedException e) {
            Log.e(TAG, e.getMessage());
        }
        // counter for RouteStopList
        int rsCounter = 0;
        // temp map for routerow
        HashMap<String, HashMap<String, String>> tempRouteRow = new HashMap<>();

        for (int i = 0; i < ETAList.etaList.size(); i++) {
            // get direction
            String dir = ETAList.etaList.get(i).get("direction");
            // get station sequence number
            String curSeq = ETAList.etaList.get(i).get("seq");
            if (dir.equals(direction)) {
                String etaSeq = ETAList.etaList.get(i).get("eta_seq");
                String fetchedEta = ETAList.etaList.get(i).get("eta");
                // stay in the same HashMap until sequence number updated
                if (!tempRouteRow.containsKey(curSeq)) {
                    tempRouteRow.put(curSeq, new HashMap<>());
                }
                if (!tempRouteRow.get(curSeq).containsKey("stop_id")) {
                    // fetch stop name using stop id
                    String stopid = RouteStopList.routeStopList.get(rsCounter).get("stopId");
                    tempRouteRow.get(curSeq).put("stop_id", stopid);
                    // find corresponding stop name
                    String stopName = null;
                    for (int j = 0; j < StopList.stopList.size(); j++) {
                        if (stopid != null) {
                            if (stopid.equals(StopList.stopList.get(j).get("stopId"))) {
                                tempRouteRow.get(curSeq).put("stop_name", StopList.stopList.get(j).get("name_en"));
                                break;
                            }
                        } else {
                            Log.e(TAG, "Null Stop Id");
                        }
                    }
                }
                tempRouteRow.get(curSeq).put(etaSeq, calculateEta(fetchedEta));
                rsCounter++;
            }
        }

        // add restructured data to RouteRowList
        for (String seq : tempRouteRow.keySet()) {
            HashMap<String, String> etas = tempRouteRow.get(seq);
            Log.d(TAG, "tempRouteRow: " + etas.toString());
            RouteRowList.addRouteRowList(
                    etas.get("stop_name"),
                    etas.get("1") != null ? etas.get("1") : "", // remain empty if = null
                    etas.get("2") != null ? etas.get("2") : "",
                    etas.get("3") != null ? etas.get("3") : "",
                    etas.get("stop_id")
            );
        }
        routeListAdapter.notifyDataSetChanged();
    }

    private String calculateEta(String fetchedEta) {
        String eta = "NA";
        if ((fetchedEta != null) && ((!fetchedEta.equals("null")))) {
            OffsetDateTime offsetDateTime = OffsetDateTime.parse(fetchedEta);
            LocalDateTime etaDateTime = offsetDateTime.atZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
            int minutesUntilArrival = (int) ChronoUnit.MINUTES.between(LocalDateTime.now(), etaDateTime);
            if (minutesUntilArrival < 0) {
                eta = "Arriving";
            } else {
                eta =  String.valueOf(minutesUntilArrival) + "min";
            }
        }
        return eta;
    }

}