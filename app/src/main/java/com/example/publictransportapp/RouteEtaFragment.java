package com.example.publictransportapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class RouteEtaFragment extends Fragment {

    private final String TAG = "RouteEtaFragment";
    private ListView routeListView;
    private RouteListAdapter routeListAdapter;
    private String route, serviceType, direction, stopId;
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
        stopId = null;

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        // get relevant data from pressing
        Bundle args = getArguments();
        if (args != null) {
            route = args.getString("route");
            serviceType = args.getString("serviceType");
            direction = args.getString("direction");
            stopId = args.getString("stop_id");
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

        Log.d(TAG, "RouteEtaList: " + ETAList.etaList.toString());

        // Create Adapter for RouteEtaList
        routeListAdapter = new RouteListAdapter(
                this.getContext(),
                ETAList.etaList
        );
        routeListView.setAdapter(routeListAdapter);

        routeListView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    }
                }
        );
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
                String eta = ETAList.etaList.get(i).get("eta");
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
                                stopName = StopList.stopList.get(j).get("name_en");
                                break;
                            }
                        } else {
                            Log.e(TAG, "Null Stop Id");
                        }
                    }
                    tempRouteRow.get(curSeq).put("stop_name", stopName);
                }
                tempRouteRow.get(curSeq).put(etaSeq, eta);
                rsCounter++;
            }
        }

        // add restructured data to RouteRowList
        for (String seq : tempRouteRow.keySet()) {
            HashMap<String, String> etas = tempRouteRow.get(seq);
            String stopName = etas.get("stop_name");
            String stopId = etas.get("stop_id");
            String eta1 = etas.get("1") != null ? etas.get("1") : ""; // keep it empty if nothing is fetched
            String eta2 = etas.get("1") != null ? etas.get("1") : ""; // keep it empty if nothing is fetched
            String eta3 = etas.get("1") != null ? etas.get("1") : ""; // keep it empty if nothing is fetched

            RouteRowList.addRouteRowList(stopName, eta1, eta2,eta3, stopId);
        }
    }

}