package com.example.publictransportapp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.publictransportapp.model.RouteEtaModel;
import com.example.publictransportapp.model.StopListModel;

import java.util.ArrayList;
import java.util.List;

public class BusListAdapter extends RecyclerView.Adapter<BusListAdapter.MyViewHolder>{

    private ArrayList<RouteEtaModel> busList;
    private ArrayList<StopListModel> stopList;

    public BusListAdapter(ArrayList<RouteEtaModel> busList, ArrayList<StopListModel> stopList) {
        this.busList = busList;
        this.stopList = stopList;
    }

    @NonNull
    @Override
    public BusListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.bus_list_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BusListAdapter.MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.route.setText(busList.get(position).getRoute());
        String stop_name = null;
        for (StopListModel stop : stopList) {
            if (stop.getStopId().equals(busList.get(position).getStopId())) {
                stop_name = stop.getName_en();
            }
        }
        holder.stop.setText(stop_name);
        holder.dest.setText(busList.get(position).getDestStopId());
        holder.eta.setText(String.valueOf(busList.get(position).getEta1()));
        holder.routeEtaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // transfer relevant bus route data to routeEtaFragment
                RouteEtaFragment routeEtaFragment = new RouteEtaFragment();
                Bundle bundle = new Bundle();

                bundle.putString("direction", busList.get(position).getDirection());
                bundle.putInt("serviceType", busList.get(position).getServiceType());
                routeEtaFragment.setArguments(bundle);

                AppCompatActivity appCompatActivity = (AppCompatActivity) view.getContext();
                FragmentTransaction fragmentTransaction = appCompatActivity.getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, routeEtaFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return busList.size();
    }

    public void updateData(List<RouteEtaModel> updateBusList) {
        busList.clear();
        busList.addAll(updateBusList);
        notifyDataSetChanged();
        Log.d("BusListAdapter", "Update Bus List: " + updateBusList.toString());
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView route,
            stop,
            dest,
            eta;
        ImageButton routeEtaButton;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            this.route = itemView.findViewById(R.id.busListRow_route_id);
            this.stop = itemView.findViewById(R.id.busListRow_stop_name);
            this.dest = itemView.findViewById(R.id.busListRow_dest_name);
            this.eta = itemView.findViewById(R.id.busListRow_eta);
            this.routeEtaButton = itemView.findViewById(R.id.busListRow_route_eta_button);
        }
    }
}
