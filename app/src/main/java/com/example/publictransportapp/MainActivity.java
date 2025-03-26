package com.example.publictransportapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity {
    // Declare the bookmark button
    FrameLayout fragmentCotainer;
    TabLayout menuBar;
    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        // initialization
        fragmentCotainer = findViewById(R.id.fragment_container);
        menuBar = findViewById(R.id.menu_bar);
        // initialize bookmark button
        button = findViewById(R.id.bookmark);

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
                    //case 1:
                        //fragment = new BookmarkActivity();
                        //break;
                }

                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab){

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab){

            }
        });

    }

    public void goBookmark(View view){
        startActivity(new Intent(this, BookmarkActivity.class));
    }
    public void goBusList(View view){
        startActivity(new Intent(this, BusListFragment.class));
    }
}