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

import com.example.publictransportapp.model.BusRowList;
import com.example.publictransportapp.model.RouteETAList;

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

        Log.d(TAG, "RouteEtaList: " + RouteETAList.routeEtaList.toString());

        // Create Adapter for RouteEtaList
        routeListAdapter = new RouteListAdapter(
                this.getContext(),
                RouteETAList.routeEtaList
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

    private void fetchRouteData() {
    }
}