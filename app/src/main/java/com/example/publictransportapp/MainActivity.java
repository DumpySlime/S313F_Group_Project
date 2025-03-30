package com.example.publictransportapp;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.publictransportapp.model.DataViewModel;
import com.example.publictransportapp.model.RouteListModel;
import com.example.publictransportapp.model.RouteStopListModel;
import com.example.publictransportapp.model.StopListModel;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    // Declare the bookmark button
    FrameLayout fragmentCotainer;
    TabLayout menuBar;
    Button button;
    ProgressDialog progressDialog;

    ArrayList<RouteListModel> routeList;
    ArrayList<RouteStopListModel> routeStopList;
    ArrayList<StopListModel> stopList;

    private DataViewModel dataViewModel;

    final String ALL_ROUTE_URL = "https://data.etabus.gov.hk/v1/transport/kmb/route/";
    final String ALL_STOP_URL = "https://data.etabus.gov.hk/v1/transport/kmb/stop/";
    final String ALL_ROUTE_STOP_URL = "https://data.etabus.gov.hk/v1/transport/kmb/route-stop/";
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

        // initialize data array
        routeList = new ArrayList<>();
        routeStopList = new ArrayList<>();
        stopList = new ArrayList<>();

        // start fetching data
        new FetchData().execute();

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
            public void onTabUnselected(TabLayout.Tab tab){

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab){

            }
        });

    }

    private class FetchData extends AsyncTask<Void, Void, Void> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("fetching data");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            fetchRoutes();
            fetchStops();
            fetchRouteStops();
            return null;
        }

        // get route data
        private void fetchRoutes() {
            try {
                URL url = new URL(ALL_ROUTE_URL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String data = "";
                String line = "";

                while ((line = bufferedReader.readLine()) != null) {
                    data += line;
                }

                if (!data.isEmpty()) {
                    JSONObject jsonObject = new JSONObject(data);
                    JSONArray routes = jsonObject.getJSONArray("data");
                    routeList.clear();
                    for (int i = 0; i < routes.length(); i++) {
                        JSONObject route = routes.getJSONObject(i);
                        routeList.add(new RouteListModel(
                                route.getString("route"),
                                route.getString("bound"),
                                route.getString("orig_en"),
                                route.getString("orig_tc"),
                                route.getString("orig_sc"),
                                route.getString("dest_en"),
                                route.getString("dest_tc"),
                                route.getString("dest_sc"),
                                route.getInt("service_type")));
                    }
                }
            } catch (MalformedURLException me) {
                Log.e("MainActivity", me.getMessage());
            } catch (JSONException je) {
                Log.e("MainActivity", je.getMessage());
            } catch (IOException ie) {
                Log.e("MainActivity", ie.getMessage());
            }
        }

        private void fetchStops() {

            try {
                // get stop data
                URL url = new URL(ALL_STOP_URL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String data = "";
                String line = "";

                while ((line = bufferedReader.readLine()) != null) {
                    data += line;
                }

                if (!data.isEmpty()) {
                    JSONObject jsonObject = new JSONObject(data);
                    JSONArray stops = jsonObject.getJSONArray("data");
                    routeList.clear();
                    for (int i = 0; i < stops.length(); i++) {
                        JSONObject stop = stops.getJSONObject(i);
                        stopList.add(new StopListModel(
                                stop.getString("stop"),
                                stop.getString("name_en"),
                                stop.getString("name_tc"),
                                stop.getString("name_sc"),
                                stop.getDouble("lat"),
                                stop.getDouble("long")));
                    }
                }
            } catch (MalformedURLException me) {
                Log.e("MainActivity", me.toString());
            } catch (JSONException je) {
                Log.e("MainActivity", je.toString());
            } catch (IOException ie) {
                Log.e("MainActivity", ie.toString());
            }
        }

        private void fetchRouteStops() {
            try {
                // get route-stop data
                URL url = new URL(ALL_STOP_URL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String data = "";
                String line = "";

                while ((line = bufferedReader.readLine()) != null) {
                    data += line;
                }

                if (!data.isEmpty()) {
                    JSONObject jsonObject = new JSONObject(data);
                    JSONArray routeStops = jsonObject.getJSONArray("data");
                    for (int i = 0; i < routeStops.length(); i++) {
                        JSONObject route = routeStops.getJSONObject(i);
                        routeStopList.add(new RouteStopListModel(
                                route.getString("route"),
                                route.getString("bound"),
                                route.getInt("service_type"),
                                route.getString("seq"),
                                route.getString("stop")
                        ));
                    }
                }
            } catch (MalformedURLException me) {
                Log.e("MainActivity", me.toString());
            } catch (JSONException je) {
                Log.e("MainActivity", je.toString());
            } catch (IOException ie) {
                Log.e("MainActivity", ie.toString());
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            dataViewModel.setRouteList(routeList);
            dataViewModel.setStopList(stopList);
            dataViewModel.setRouteStopList(routeStopList);

            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }
    }
}