package com.example.publictransportapp;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.publictransportapp.fragment.BookmarkFragment;
import com.example.publictransportapp.fragment.BusListFragment;
import com.example.publictransportapp.fragment.SearchFragment;
import com.example.publictransportapp.fragment.ShortestPathFragment;
import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity {
    // Declare the bookmark button
    FrameLayout fragmentCotainer;
    TabLayout menuBar;
    TextView titleBar;
    Button bookmarkButton;
    ImageButton filterButton;
    double detectionRange;
    private String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        // initialization
        fragmentCotainer = findViewById(R.id.fragment_container);
        menuBar = findViewById(R.id.menu_bar);
        titleBar = findViewById(R.id.title_bar);
        // initialize bookmark button
        bookmarkButton = findViewById(R.id.bookmark_tab);

        // initialize filter button
        filterButton = findViewById(R.id.filter_button);
        filterButton.setOnClickListener(v -> showUpdateRangeDialog());
        detectionRange = 200;

        // start fetching data
        JsonHandlerThread jsonHandlerThread = new JsonHandlerThread();
        jsonHandlerThread.start();

        try {
            jsonHandlerThread.join();
        } catch (InterruptedException e) {
            Log.e(TAG, e.getMessage());
        }

        //load fragment_bus_list when start app, and start the default range as 200m
        loadBusListFragment(detectionRange);

        // set up tab listener
        menuBar.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab){
                switch (tab.getPosition()) {
                    case 0:
                        titleBar.setText(R.string.busList_button);
                        filterButton.setVisibility(View.VISIBLE);
                        loadBusListFragment(detectionRange);
                        break;
                    case 1:
                        titleBar.setText(R.string.bookmark_button);
                        filterButton.setVisibility(View.INVISIBLE);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, new BookmarkFragment())
                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                .commit();
                        break;
                    case 2:
                        titleBar.setText(R.string.searchRoute_button);
                        filterButton.setVisibility(View.INVISIBLE);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, new SearchFragment())
                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                .commit();
                        break;
                    case 3:
                        titleBar.setText(R.string.shortestPath_button);
                        filterButton.setVisibility(View.INVISIBLE);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, new ShortestPathFragment())
                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                .commit();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab){}

            @Override
            public void onTabReselected(TabLayout.Tab tab){
                Fragment fragment = null;
                switch (tab.getPosition()) {
                    case 0:
                        titleBar.setText(R.string.busList_button);
                        filterButton.setVisibility(View.VISIBLE);
                        loadBusListFragment(detectionRange);
                        break;
                    case 1:
                        titleBar.setText(R.string.bookmark_button);
                        filterButton.setVisibility(View.INVISIBLE);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, new BookmarkFragment())
                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                .commit();
                        break;
                    case 2:
                        titleBar.setText(R.string.searchRoute_button);
                        filterButton.setVisibility(View.INVISIBLE);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, new SearchFragment())
                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                .commit();
                        break;
                    case 3:
                        titleBar.setText(R.string.shortestPath_button);
                        filterButton.setVisibility(View.INVISIBLE);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, new ShortestPathFragment())
                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                .commit();
                }
            }
        });


    }

    private void showUpdateRangeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        View filterDialogView = getLayoutInflater().inflate(R.layout.dialog_range_filter, null);

        SeekBar rangeSeekBar = filterDialogView.findViewById(R.id.range_seekbar);
        TextView rangeValue = filterDialogView.findViewById(R.id.range_value);

        rangeSeekBar.setMax(8);

        int progress = (int) ((detectionRange / 50) - 2);
        rangeSeekBar.setProgress(progress);
        rangeValue.setText(detectionRange + " m");

        rangeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                int range = (i + 2) * 50;
                rangeValue.setText(range + " m");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        builder.setView(filterDialogView)
                .setTitle("Update Detection Range")
                .setPositiveButton("Apply", (dialog, which) -> {
                    detectionRange = (rangeSeekBar.getProgress() + 2) * 50;
                    loadBusListFragment(detectionRange);
                })
                .setNegativeButton("Cancel", null);

        builder.create().show();
    }

    private void loadBusListFragment(double range) {
        Bundle bundle = new Bundle();
        bundle.putDouble("detection_range", range);
        BusListFragment busListFragment = new BusListFragment();
        busListFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, busListFragment)
                .addToBackStack(null)
                .commit();
    }
}