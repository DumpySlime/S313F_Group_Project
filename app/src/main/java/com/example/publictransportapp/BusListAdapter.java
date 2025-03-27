package com.example.publictransportapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.publictransportapp.model.BusRowModel;

import java.util.ArrayList;

public class BusListAdapter extends RecyclerView.Adapter<BusListAdapter.MyViewHolder>{

    ArrayList<BusRowModel> busRowModels;

    public BusListAdapter(ArrayList<BusRowModel> busRowModels) {
        this.busRowModels = busRowModels;
    }

    @NonNull
    @Override
    public BusListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.bus_list_row, parent, false);
        return new BusListAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BusListAdapter.MyViewHolder holder, int position) {
        holder.route.setText(busRowModels.get(position).getRoute());
        holder.stop.setText(busRowModels.get(position).getStop());
        holder.dest.setText(busRowModels.get(position).getDest());
        holder.eta.setText(busRowModels.get(position).getEta());
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView route,
            stop,
            dest,
            eta;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            this.route = itemView.findViewById(R.id.busListRow_route_id);
            this.stop = itemView.findViewById(R.id.busListRow_stop_name);
            this.dest = itemView.findViewById(R.id.busListRow_dest_name);
            this.eta = itemView.findViewById(R.id.busListRow_eta);
        }
    }
}
