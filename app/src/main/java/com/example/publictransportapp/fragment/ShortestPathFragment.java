package com.example.publictransportapp.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.publictransportapp.R;
import com.example.publictransportapp.api.RetrofitClient;
import com.example.publictransportapp.model.RouteStop;
import com.example.publictransportapp.model.RouteStopResponse;
import com.example.publictransportapp.model.Stop;
import com.example.publictransportapp.model.StopResponse;
import com.example.publictransportapp.util.Dijkstra;
import com.example.publictransportapp.util.GraphBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShortestPathFragment extends Fragment implements
        RouteSearchFragment.OnRouteSearchListener,
        RouteResultFragment.OnBackToSearchListener {

    private ProgressBar progressBar;

    private List<Stop> realStops = new ArrayList<>();
    private List<RouteStop> realRouteStops = new ArrayList<>();
    private Map<String, List<String>> routeGraph;
    private Map<String, Stop> stopIdMap = new HashMap<>();
    private Map<String, List<RouteStop>> routeDetails = new HashMap<>();

    // Fragment references
    private RouteSearchFragment searchFragment;
    private RouteResultFragment resultFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Log.d("BusListFragment", "Initialize onCreateView");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_progress_bar, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews();

        // Initialize and show loading Fragment
        showLoadingFragment(getString(R.string.data_loading));

        // Load data
        loadData();
    }

    private void initViews() {
        progressBar = requireActivity().findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void loadData() {
        try {
            loadStopsData();
            loadRouteStopsData();
        } catch (Exception e) {
            handleError(getString(R.string.loading_error, e.getMessage()));
            // Try loading from local data if online loading fails
            loadLocalData();
        }
    }

    private void loadStopsData() {
        RetrofitClient.getApi().getAllStopsWithFormat("json").enqueue(new Callback<StopResponse>() {
            @Override
            public void onResponse(Call<StopResponse> call, Response<StopResponse> response) {
                if (handleResponse(response)) {
                    realStops = response.body().getData();
                    System.out.println("++++++++++++++");
                    for (Stop stop : realStops) {
                        stopIdMap.put(stop.getStop(), stop);
                    }
                    checkDataReady();
                } else {
                    // Try local data
                    loadLocalData();
                }
            }

            @Override
            public void onFailure(Call<StopResponse> call, Throwable t) {
                handleError(getString(R.string.stations_loading_failed, t.getMessage()));
                // Try local data
                loadLocalData();
            }
        });
    }

    private void loadRouteStopsData() {
        RetrofitClient.getApi().getAllRouteStopsWithFormat("json").enqueue(new Callback<RouteStopResponse>() {
            @Override
            public void onResponse(Call<RouteStopResponse> call, Response<RouteStopResponse> response) {
                if (handleResponse(response)) {
                    realRouteStops = response.body().getData();
                    processRouteDetails();
                    checkDataReady();
                }
            }

            @Override
            public void onFailure(Call<RouteStopResponse> call, Throwable t) {
                handleError(getString(R.string.routes_loading_failed, t.getMessage()));
            }
        });
    }

    private void processRouteDetails() {
        for (RouteStop rs : realRouteStops) {
            String routeKey = rs.getRoute() + "|" + rs.getBound() + "|" + rs.getServiceType();
            routeDetails.computeIfAbsent(routeKey, k -> new ArrayList<>()).add(rs);
        }
    }

    private void checkDataReady() {
        if (!realStops.isEmpty() && !realRouteStops.isEmpty()) {
            requireActivity().runOnUiThread(() -> {
                progressBar.setVisibility(View.GONE);
                buildRouteGraph();
                showSearchFragment();
            });
        }
    }

    private void buildRouteGraph() {
        GraphBuilder graphBuilder = new GraphBuilder();
        routeGraph = graphBuilder.buildGraph(realRouteStops);
    }

    // Fragment management methods
    private void showLoadingFragment(String message) {
        LoadingFragment loadingFragment = LoadingFragment.newInstance(message);
        getChildFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, loadingFragment)
                .commit();
    }

    private void showSearchFragment() {
        if (searchFragment == null) {
            searchFragment = new RouteSearchFragment();
        }

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer, searchFragment);
        transaction.commit();

        // Problem fix: Make sure Fragment is added before setting autocomplete data
        getChildFragmentManager().executePendingTransactions();

        // Set autocomplete data
        if (realStops != null && !realStops.isEmpty()) {
            searchFragment.setupAutocomplete(realStops);
        } else {
            Log.e("ShortestPathFragment", "realStop is null or empty");
        }
    }

    private void showResultFragment(String resultText) {
        if (resultFragment == null) {
            resultFragment = new RouteResultFragment();
        }

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer, resultFragment);
        // Don't add to back stack to avoid showing loading screen when returning
        // transaction.addToBackStack(null);
        transaction.commit();

        // Make sure Fragment is added
        getChildFragmentManager().executePendingTransactions();

        // Set result text
        resultFragment.setResultText(resultText);
    }

    // RouteSearchFragment.OnRouteSearchListener implementation
    @Override
    public void onSearchRoute(String startName, String endName) {
        findRealPath(startName, endName);
    }

    // RouteResultFragment.OnBackToSearchListener implementation
    @Override
    public void onBackToSearch() {
        // Don't use back stack, directly switch to search Fragment
        showSearchFragment();
    }

    private void findRealPath(String startName, String endName) {
        Stop start = findStopByName(startName);
        Stop end = findStopByName(endName);

        if (start == null || end == null) {
            showError(getString(R.string.invalid_station));
            return;
        }

        // Show loading Fragment
        showLoadingFragment(getString(R.string.calculating_route));

        // Make sure loading Fragment is displayed
        getChildFragmentManager().executePendingTransactions();

        // Add delay to ensure UI update
        new Handler().postDelayed(() -> {
            // Calculate path in background thread
            new Thread(() -> {
                try {
                    Dijkstra dijkstra = new Dijkstra();
                    List<String> path = dijkstra.shortestPath(routeGraph, start.getStop(), end.getStop());

                    // Handle result on UI thread
                    requireActivity().runOnUiThread(() -> handlePathResult(path, start, end));
                } catch (Exception e) {
                    requireActivity().runOnUiThread(() -> {
                        showError(getString(R.string.route_calculation_error, e.getMessage()));
                        showSearchFragment();
                    });
                }
            }).start();
        }, 500); // Short delay to ensure loading screen is shown
    }

    private void handlePathResult(List<String> path, Stop start, Stop end) {
        if (path.isEmpty()) {
            showError(getString(R.string.no_route_found));
            showSearchFragment();
            return;
        }

        List<RouteSegment> routeSegments = findRouteSegments(path);
        if (routeSegments.isEmpty()) {
            showError(getString(R.string.cannot_determine_route));
            showSearchFragment();
            return;
        }

        String routeText = buildRouteText(routeSegments, start, end);
        showResultFragment(routeText);
    }

    private String buildRouteText(List<RouteSegment> segments, Stop start, Stop end) {
        StringBuilder sb = new StringBuilder();
        sb.append(getString(R.string.route_from_to, start.getNameEn(), end.getNameEn()))
                .append("\n\n")
                .append(getString(R.string.routes_needed, segments.size()))
                .append("\n\n");

        for (int i = 0; i < segments.size(); i++) {
            RouteSegment segment = segments.get(i);
            String from = stopIdMap.get(segment.getPath().get(0)).getNameEn();
            String to = stopIdMap.get(segment.getPath().get(segment.getPath().size() - 1)).getNameEn();

            sb.append(getString(R.string.route_number, i + 1, segment.getRouteNumber())).append("\n")
                    .append(getString(R.string.board_at, from)).append("\n")
                    .append(getString(R.string.alight_at, to)).append("\n")
                    .append(getString(R.string.stops_count, segment.getPath().size())).append("\n\n");
        }

        return sb.toString();
    }

    private List<RouteSegment> findRouteSegments(List<String> path) {
        List<RouteSegment> segments = new ArrayList<>();
        int currentIndex = 0;

        while (currentIndex < path.size() - 1) {
            RouteSegment segment = findLongestRoute(path, currentIndex);
            if (segment == null) break;

            segments.add(segment);
            currentIndex = segment.getEndIndex();
        }
        return segments;
    }

    private RouteSegment findLongestRoute(List<String> path, int startIndex) {
        RouteSegment bestSegment = null;
        int maxLength = 0;

        for (String routeKey : routeDetails.keySet()) {
            List<RouteStop> routeStops = routeDetails.get(routeKey);
            List<String> routeStopIds = new ArrayList<>();

            for (RouteStop rs : routeStops) {
                routeStopIds.add(rs.getStop());
            }

            int length = 0;
            int i = startIndex;

            while (i < path.size() && routeStopIds.contains(path.get(i))) {
                length++;
                i++;
            }

            if (length > 1 && length > maxLength) {
                maxLength = length;
                String routeNumber = routeKey.split("\\|")[0];
                List<String> segmentPath = path.subList(startIndex, startIndex + length);
                bestSegment = new RouteSegment(routeNumber, startIndex, startIndex + length, segmentPath);
            }
        }

        return bestSegment;
    }

    private Stop findStopByName(String name) {
        for (Stop stop : realStops) {
            if (stop.getNameEn().equalsIgnoreCase(name)) {
                return stop;
            }
        }
        return null;
    }

    private boolean handleResponse(Response<?> response) {
        if (!response.isSuccessful() || response.body() == null) {
            String errorMessage = getString(R.string.server_error);
            try {
                if (response.errorBody() != null) {
                    errorMessage += "\n" + response.errorBody().string();
                }
                errorMessage += "\nCode: " + response.code();

                // Check if it's a 403 error (blocked)
                if (response.code() == 403) {
                    errorMessage = getString(R.string.api_denied_message);
                }
            } catch (Exception e) {
                errorMessage += "\n" + getString(R.string.error_details_failed, e.getMessage());
            }
            showError(errorMessage);
            return false;
        }
        return true;
    }

    private void loadLocalData() {
        try {
            String stopsJson = loadJSONFromAsset("stops.json");
            String routeStopsJson = loadJSONFromAsset("route_stops.json");

            if (stopsJson != null && routeStopsJson != null) {
                // Code to parse JSON data into objects needed here
                // Simple example:
                showError(getString(R.string.api_rejected));
                showSearchFragment();
            } else {
                showError(getString(R.string.no_local_data));
            }
        } catch (Exception e) {
            handleError(getString(R.string.local_data_loading_failed, e.getMessage()));
        }
    }

    private String loadJSONFromAsset(String fileName) {
        String json = null;
        try {
            InputStream is = requireActivity().getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            handleError(getString(R.string.read_local_file_failed, ex.getMessage()));
            return null;
        }
        return json;
    }

    private void showLoading(String message) {
        requireActivity().runOnUiThread(() -> {
            progressBar.setVisibility(View.VISIBLE);
            showLoadingFragment(message);
        });
    }

    private void showError(String message) {
        requireActivity().runOnUiThread(() -> {
            progressBar.setVisibility(View.GONE);
            if (searchFragment != null && searchFragment.isAdded()) {
                Toast.makeText(requireActivity(), "❌ " + message, Toast.LENGTH_LONG).show();
            } else {
                showLoadingFragment("❌ " + message);
            }
        });
    }

    private void handleError(String message) {
        requireActivity().runOnUiThread(() -> {
            progressBar.setVisibility(View.GONE);
            showLoadingFragment("⚠️ " + message);
        });
    }

    private static class RouteSegment {
        private final String routeNumber;
        private final int startIndex;
        private final int endIndex;
        private final List<String> path;

        public RouteSegment(String routeNumber, int startIndex, int endIndex, List<String> path) {
            this.routeNumber = routeNumber;
            this.startIndex = startIndex;
            this.endIndex = endIndex;
            this.path = path;
        }

        public String getRouteNumber() { return routeNumber; }
        public int getStartIndex() { return startIndex; }
        public int getEndIndex() { return endIndex; }
        public List<String> getPath() { return path; }
    }
}