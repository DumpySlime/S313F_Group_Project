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
import com.example.publictransportapp.model.RouteListModel;
import com.example.publictransportapp.model.StopListModel;

import java.util.ArrayList;
import java.util.List;

public class BusListAdapter extends RecyclerView.Adapter<BusListAdapter.MyViewHolder>{

    private ArrayList<RouteEtaModel> busList;
    private ArrayList<StopListModel> stopList;
    private ArrayList<RouteListModel> routeList;

    public BusListAdapter(ArrayList<RouteEtaModel> busList, ArrayList<StopListModel> stopList, ArrayList<RouteListModel> routeList) {
        this.busList = busList;
        this.stopList = stopList;
        this.routeList = routeList;
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
        // setup bus route
        holder.route.setText(busList.get(position).getRoute());

        // setup stop name
        StopListModel stop = null;
        for (StopListModel stopModel : stopList) {
            if (stop.getStopId().equals(busList.get(position).getStopId())) {
                stop = stopModel;
            }
        }
        if (stop != null) {
            holder.stop_name.setText(stop.getName_en());
        } else {
            holder.stop_name.setText("Unknown Stop");
        }

        // setup bus route destination
        String destName = "Unknown Destination";
        for (RouteListModel routeModel : routeList) {
            if ((routeModel.getRoute().equals(busList.get(position).getRoute())) &&
                    (routeModel.getDirection().equals(busList.get(position).getDirection())) &&
                    (routeModel.getServiceType() == busList.get(position).getServiceType())) {
                destName = routeModel.getDest_en();
                break;
            }
        }
        holder.dest.setText(destName);
        // setup ETA
        holder.eta.setText(String.valueOf(busList.get(position).getEta1()));
        // setup button to route info
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
            stop_name,
            dest,
            eta;
        ImageButton routeEtaButton;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            this.route = itemView.findViewById(R.id.busListRow_route_id);
            this.stop_name = itemView.findViewById(R.id.busListRow_stop_name);
            this.dest = itemView.findViewById(R.id.busListRow_dest_name);
            this.eta = itemView.findViewById(R.id.busListRow_eta);
            this.routeEtaButton = itemView.findViewById(R.id.busListRow_route_eta_button);
        }
    }
}
