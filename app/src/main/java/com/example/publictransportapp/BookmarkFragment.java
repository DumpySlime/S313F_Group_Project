package com.example.publictransportapp;

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

        return view;
    }

    private void showAddStopDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_stop, null);
        builder.setView(dialogView);

        EditText routeNumberInput = dialogView.findViewById(R.id.input_route_number);
        EditText distinctionInput = dialogView.findViewById(R.id.input_distinction);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String routeNumber = routeNumberInput.getText().toString();
            String distinction = distinctionInput.getText().toString();
            addStopToList(routeNumber, distinction);
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.create().show();
    }

    private void addStopToList(String routeNumber, String distinction) {
        View stopItem = LayoutInflater.from(requireContext()).inflate(R.layout.item_stop, stopListContainer, false);
        TextView stopDetails = stopItem.findViewById(R.id.text_stop_details);
        Button deleteButton = stopItem.findViewById(R.id.btn_delete_stop);

        stopDetails.setText("Route: " + routeNumber + ", Distinction: " + distinction);

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
            addPlaceToList(location, category);
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
}