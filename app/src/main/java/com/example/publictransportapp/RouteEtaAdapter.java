package com.example.publictransportapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.publictransportapp.model.RouteEtaModel;

import java.util.ArrayList;

public class RouteEtaAdapter extends RecyclerView.Adapter<RouteEtaAdapter.MyViewHolder>{

    private ArrayList<RouteEtaModel> routeEtaModels;

    public RouteEtaAdapter(ArrayList<RouteEtaModel> routeEtaModels) {
        this.routeEtaModels = routeEtaModels;
    }

    @NonNull
    @Override
    public RouteEtaAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.route_eta_row, parent, false);
        return new RouteEtaAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RouteEtaAdapter.MyViewHolder holder, int position) {
        holder.stop.setText(routeEtaModels.get(position).getStopId());
        holder.eta1.setText(routeEtaModels.get(position).getEta1());
        holder.eta2.setText(routeEtaModels.get(position).getEta2());
        holder.eta3.setText(routeEtaModels.get(position).getEta3());
    }

    @Override
    public int getItemCount() {
        return routeEtaModels.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView stop,
            eta1,
            eta2,
            eta3;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            this.stop = itemView.findViewById(R.id.routeEtaRow_station_name);
            this.eta1 = itemView.findViewById(R.id.routeEtaRow_eta1);
            this.eta2 = itemView.findViewById(R.id.routeEtaRow_eta2);
            this.eta3 = itemView.findViewById(R.id.routeEtaRow_eta3);
        }
    }
}
