package com.example.publictransportapp;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.publictransportapp.model.BusRoute;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {
    private RadioGroup rgDirection;
    private EditText etSearchInput;
    private Button btnSearch;
    private RecyclerView rvResults;
    private BusRouteAdapter adapter;
    private String direction = "outbound";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        initViews(view);
        setupListeners();
        return view;
    }

    private void initViews(View view) {
        rgDirection = view.findViewById(R.id.rgDirection);
        etSearchInput = view.findViewById(R.id.etSearchInput);
        btnSearch = view.findViewById(R.id.btnSearch);
        rvResults = view.findViewById(R.id.rvResults);

        rvResults.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new BusRouteAdapter();
        rvResults.setAdapter(adapter);
    }

    private void setupListeners() {
        rgDirection.setOnCheckedChangeListener((group, checkedId) -> {
            direction = (checkedId == R.id.rbOutbound) ? "outbound" : "inbound";
        });

        btnSearch.setOnClickListener(v -> performSearch());
    }

    private void performSearch() {
        String route = etSearchInput.getText().toString().trim();
        if (route.isEmpty()) {
            Toast.makeText(getContext(), "Please enter a bus route", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(() -> {
            try {
                String serviceType = "1";
                String json = ApiClient.getRouteInfo(route, direction, serviceType);
                BusRoute busRoute = JsonParser.parseBusRoute(json);

                requireActivity().runOnUiThread(() -> {
                    rvResults.setVisibility(View.VISIBLE);
                    List<BusRoute> routes = new ArrayList<>();
                    routes.add(busRoute);
                    adapter.setData(routes);
                });
            } catch (Exception e) {
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
            }
        }).start();
    }
}