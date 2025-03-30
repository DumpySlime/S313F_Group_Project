package com.example.publictransportapp;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity {
    // Declare the bookmark button
    FrameLayout fragmentCotainer;
    TabLayout menuBar;
    Button button;
    private String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        // initialization
        fragmentCotainer = findViewById(R.id.fragment_container);
        menuBar = findViewById(R.id.menu_bar);
        // initialize bookmark button
        button = findViewById(R.id.bookmark_tab);

        // start fetching data
        JsonHandlerThread jsonHandlerThread = new JsonHandlerThread();
        jsonHandlerThread.start();

        try {
            jsonHandlerThread.join();
        } catch (InterruptedException e) {
            Log.e(TAG, e.getMessage());
        }

        //load fragment_bus_list when start app
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new BusListFragment())
                .addToBackStack(null)
                .commit();

        // set up tab listener
        menuBar.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab){
                Fragment fragment = null;
                switch (tab.getPosition()) {
                    case 0:
                        fragment = new BusListFragment();
                        break;
                    case 1:
                        fragment = new BookmarkFragment();
                        break;
                }

                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab){}

            @Override
            public void onTabReselected(TabLayout.Tab tab){}
        });


    }
}