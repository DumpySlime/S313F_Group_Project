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
            view = inflater.inflate(R.layout.bus_list_row, parent, false);
        }
        //Log.d("BusRowAdapter","getView");
        TextView routeTextView = view.findViewById(R.id.busListRow_route);
        TextView stopTextView = view.findViewById(R.id.busListRow_stop_name);
        TextView destTextView = view.findViewById(R.id.busListRow_dest_name);
        TextView etaTextView = view.findViewById(R.id.busListRow_eta);

        routeTextView.setText(data.get(position).get("route"));
        stopTextView.setText(data.get(position).get("stop_name"));
        destTextView.setText(data.get(position).get("dest"));
        etaTextView.setText(data.get(position).get("eta"));

        return view;
    }
}
