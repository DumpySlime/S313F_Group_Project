package com.example.publictransportapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.publictransportapp.model.BusRowList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BusRowAdapter extends BaseAdapter {

    private final Context context;
    private final List<HashMap<String, String>> data;

    public BusRowAdapter(Context context, List<HashMap<String, String>> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size()-5;
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
        FrameLayout busListRowLayout = view.findViewById(R.id.busListRow_Layout);

        routeTextView.setText(data.get(position).get("route"));
        stopTextView.setText(data.get(position).get("stop_name"));
        destTextView.setText(data.get(position).get("dest"));
        etaTextView.setText(data.get(position).get("eta"));

        if (position % 2 == 0) {
            busListRowLayout.setBackgroundColor(0xD4D4D4D4);
        } else {
            busListRowLayout.setBackgroundColor(0xFFFFFFFF);
        }

        return view;
    }
}
