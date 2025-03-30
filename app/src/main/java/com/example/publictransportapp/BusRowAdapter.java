package com.example.publictransportapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.publictransportapp.model.BusRowList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BusRowAdapter extends BaseAdapter {

    private final Context context;
    private final ArrayList<HashMap<String, String>> data;

    public BusRowAdapter(Context context, ArrayList<HashMap<String, String>> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size()-2;
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

        TextView routeTextView = view.findViewById(R.id.busListRow_route_id);
        TextView stopTextView = view.findViewById(R.id.busListRow_stop_name);
        TextView destTextView = view.findViewById(R.id.busListRow_dest_name);
        TextView etaTextView = view.findViewById(R.id.busListRow_eta);

        routeTextView.setText(BusRowList.ROUTE);
        stopTextView.setText(BusRowList.STOP_NAME);
        destTextView.setText(BusRowList.DEST);
        etaTextView.setText(BusRowList.ETA);

        return view;
    }
}
