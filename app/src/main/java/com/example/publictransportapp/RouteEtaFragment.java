package com.example.publictransportapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.publictransportapp.model.RouteEtaModel;

import java.util.ArrayList;

public class RouteEtaFragment extends Fragment {

    private ArrayList<RouteEtaModel> routeRecyclerList;
    private RouteEtaAdapter routeEtaAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_route_eta, container, false);

        RecyclerView routeEtaView = view.findViewById(R.id.route_eta_recycle_view);

        routeEtaView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        routeRecyclerList = new ArrayList<>();
        routeEtaAdapter = new RouteEtaAdapter(routeRecyclerList);
        routeEtaView.setAdapter(routeEtaAdapter);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {

    }
}