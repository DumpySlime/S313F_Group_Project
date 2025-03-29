package com.example.publictransportapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.publictransportapp.model.Place;
import com.example.publictransportapp.model.Stop;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

public class BookmarkFragment extends Fragment {

    private LinearLayout stopListContainer;
    private LinearLayout placeListContainer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the fragment layout
        View view = inflater.inflate(R.layout.fragment_bookmark, container, false);

        stopListContainer = view.findViewById(R.id.stop_list_container);
        placeListContainer = view.findViewById(R.id.place_list_container);

        Button addStopButton = view.findViewById(R.id.btn_add_stop);
        Button addPlaceButton = view.findViewById(R.id.btn_add_place);

        addStopButton.setOnClickListener(v -> showAddStopDialog());
        addPlaceButton.setOnClickListener(v -> showAddPlaceDialog());

        loadStopsFromPreferences();  // Load stops
        loadPlacesFromPreferences();  // Load places

        return view;
    }

    private void showAddStopDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_stop, null);
        builder.setView(dialogView);

        EditText routeNumberInput = dialogView.findViewById(R.id.input_route_number);
        EditText distinctionInput = dialogView.findViewById(R.id.input_distinction);
        EditText stopCategoryInput = dialogView.findViewById(R.id.input_stop_category);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String routeNumber = routeNumberInput.getText().toString();
            String distinction = distinctionInput.getText().toString();
            String stopCategory = stopCategoryInput.getText().toString();
            Stop stop = new Stop(routeNumber, distinction, stopCategory);
            addStopToList(routeNumber, distinction, stopCategory);
            saveStopToPreferences(stop); // Save to SharedPreferences
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.create().show();
    }

    private void addStopToList(String routeNumber, String distinction, String stopCategory) {
        View stopItem = LayoutInflater.from(requireContext()).inflate(R.layout.item_stop, stopListContainer, false);
        TextView stopDetails = stopItem.findViewById(R.id.text_stop_details);
        Button deleteButton = stopItem.findViewById(R.id.btn_delete_stop);

        stopDetails.setText("Route: " + routeNumber + ", Distinction: " + distinction + ", Category: " + stopCategory);

        // Set delete functionality
        deleteButton.setOnClickListener(v -> stopListContainer.removeView(stopItem));

        stopListContainer.addView(stopItem);
    }

    private void showAddPlaceDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_place, null);
        builder.setView(dialogView);

        EditText locationInput = dialogView.findViewById(R.id.input_location);
        EditText categoryInput = dialogView.findViewById(R.id.input_category);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String location = locationInput.getText().toString();
            String category = categoryInput.getText().toString();
            Place place = new Place(location, category);
            addPlaceToList(location, category);
            savePlaceToPreferences(place); // Save to SharedPreferences
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.create().show();

    }

    private void addPlaceToList(String location, String category) {
        View placeItem = LayoutInflater.from(requireContext()).inflate(R.layout.item_place, placeListContainer, false);
        TextView placeDetails = placeItem.findViewById(R.id.text_place_details);
        Button deleteButton = placeItem.findViewById(R.id.btn_delete_place);

        placeDetails.setText("Location: " + location + ", Category: " + category);

        // Set delete functionality
        deleteButton.setOnClickListener(v -> placeListContainer.removeView(placeItem));

        placeListContainer.addView(placeItem);
    }
    private void loadStopsFromPreferences() {
        SharedPreferences prefs = requireContext().getSharedPreferences("bookmarks", Context.MODE_PRIVATE);
        String stopsJson = prefs.getString("stops", "[]");
        List<Stop> stops = new Gson().fromJson(stopsJson, new TypeToken<List<Stop>>(){}.getType());

        for (Stop stop : stops) {
            addStopToList(stop.getRouteNumber(), stop.getDistinction(), stop.getStopCategory());
        }
    }

    private void loadPlacesFromPreferences() {
        SharedPreferences prefs = requireContext().getSharedPreferences("bookmarks", Context.MODE_PRIVATE);
        String placesJson = prefs.getString("places", "[]");
        List<Place> places = new Gson().fromJson(placesJson, new TypeToken<List<Place>>(){}.getType());

        for (Place place : places) {
            addPlaceToList(place.getLocation(), place.getCategory());
        }
    }
    private void saveStopToPreferences(Stop stop) {
        SharedPreferences prefs = requireContext().getSharedPreferences("bookmarks", Context.MODE_PRIVATE);
        String stopsJson = prefs.getString("stops", "[]");

        // Convert JSON to a List, add new stop, and save it back
        List<Stop> stops = new Gson().fromJson(stopsJson, new TypeToken<List<Stop>>(){}.getType());
        stops.add(stop);

        String newStopsJson = new Gson().toJson(stops);
        prefs.edit().putString("stops", newStopsJson).apply();
    }

    private void savePlaceToPreferences(Place place) {
        SharedPreferences prefs = requireContext().getSharedPreferences("bookmarks", Context.MODE_PRIVATE);
        String placesJson = prefs.getString("places", "[]");

        // Convert JSON to a List, add new place, and save it back
        List<Place> places = new Gson().fromJson(placesJson, new TypeToken<List<Place>>(){}.getType());
        places.add(place);

        String newPlacesJson = new Gson().toJson(places);
        prefs.edit().putString("places", newPlacesJson).apply();
    }
}