package com.example.publictransportapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.publictransportapp.model.BusRowModel;

import java.util.ArrayList;

public class BusListAdapter extends RecyclerView.Adapter<BusListAdapter.MyViewHolder>{

    private ArrayList<BusRowModel> busRowModels;

    public BusListAdapter(ArrayList<BusRowModel> busRowModels) {
        this.busRowModels = busRowModels;
    }

    @NonNull
    @Override
    public BusListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.bus_list_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BusListAdapter.MyViewHolder holder, int position) {
        holder.route.setText(busRowModels.get(position).getRoute());
        holder.stop.setText(busRowModels.get(position).getStop());
        holder.dest.setText(busRowModels.get(position).getDest());
        holder.eta.setText(busRowModels.get(position).getEta());
        holder.routeEtaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // transfer relevant bus route data to routeEtaFragment
                RouteEtaFragment routeEtaFragment = new RouteEtaFragment();
                Bundle bundle = new Bundle();

                bundle.putString("direction", busRowModels.get(position).getDirection());
                bundle.putInt("serviceType", busRowModels.get(position).getServiceType());
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
        return busRowModels.size();
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
