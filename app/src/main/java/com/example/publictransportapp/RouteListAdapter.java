package com.example.publictransportapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class RouteListAdapter extends BaseAdapter {

    private final Context context;
    private final ArrayList<HashMap<String, String>> data;

    public RouteListAdapter(Context context, ArrayList<HashMap<String, String>> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size()-3;
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.route_eta_row, parent, false);
        }
        //Log.d("BusRowAdapter","getView");
        TextView stopNameTextView = view.findViewById(R.id.routeEtaRow_station_name);
        TextView eta1TextView = view.findViewById(R.id.routeEtaRow_eta1);
        TextView eta2TextView = view.findViewById(R.id.routeEtaRow_eta2);
        TextView eta3TextView = view.findViewById(R.id.routeEtaRow_eta3);

        stopNameTextView.setText(data.get(position).get("stop_name"));
        eta1TextView.setText(data.get(position).get("eta1"));
        eta2TextView.setText(data.get(position).get("eta2"));
        eta3TextView.setText(data.get(position).get("eta3"));

        return view;
    }
}
