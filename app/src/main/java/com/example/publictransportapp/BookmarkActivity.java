package com.example.publictransportapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;

public class BookmarkActivity extends AppCompatActivity {
    private LinearLayout stopListContainer;
    private LinearLayout placeListContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark);

        stopListContainer = findViewById(R.id.stop_list_container);
        placeListContainer = findViewById(R.id.place_list_container);

        Button addStopButton = findViewById(R.id.btn_add_stop);
        Button addPlaceButton = findViewById(R.id.btn_add_place);

        addStopButton.setOnClickListener(v -> showAddStopDialog());
        addPlaceButton.setOnClickListener(v -> showAddPlaceDialog());
    }

    private void showAddStopDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_stop, null);
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
        View stopItem = LayoutInflater.from(this).inflate(R.layout.item_stop, stopListContainer, false);
        TextView stopDetails = stopItem.findViewById(R.id.text_stop_details);
        Button deleteButton = stopItem.findViewById(R.id.btn_delete_stop);

        stopDetails.setText("Route: " + routeNumber + ", Distinction: " + distinction);

        // Set delete functionality
        deleteButton.setOnClickListener(v -> stopListContainer.removeView(stopItem));

        stopListContainer.addView(stopItem);
    }

    private void showAddPlaceDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_place, null);
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
        View placeItem = LayoutInflater.from(this).inflate(R.layout.item_place, placeListContainer, false);
        TextView placeDetails = placeItem.findViewById(R.id.text_place_details);
        Button deleteButton = placeItem.findViewById(R.id.btn_delete_place);

        placeDetails.setText("Location: " + location + ", Category: " + category);

        // Set delete functionality
        deleteButton.setOnClickListener(v -> placeListContainer.removeView(placeItem));

        placeListContainer.addView(placeItem);
    }
}