package com.example.publictransportapp;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String ROUTE_INFO_URL = "https://data.etabus.gov.hk/v1/transport/kmb/route/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        //Initialize menu bar
        Toolbar titleBar = findViewById(R.id.title_bar);
        setSupportActionBar(titleBar);

        //Initialize list view
        ListView listView = findViewById(R.id.list_view);
        //Get bus info

        List<String> buses = new ArrayList<String>();
        for (int i = 1; i < 11; i++){
            buses.add(Integer.toString(i));
        }
        ArrayAdapter arrayAdapter = new ArrayAdapter(getApplicationContext(), R.layout.activity_main, buses);
        listView.setAdapter(arrayAdapter);

    }
}