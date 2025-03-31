package com.example.publictransportapp;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.publictransportapp.model.BusRoute;

import java.util.List;

public class BusRouteAdapter extends RecyclerView.Adapter<BusRouteAdapter.ViewHolder> {
    private List<BusRoute> busRoutes;

    public void setData(List<BusRoute> busRoutes) {
        this.busRoutes = busRoutes;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_bus_route, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BusRoute route = busRoutes.get(position);
        holder.tvRouteNumber.setText(route.getRouteNumber());
        holder.origTc.setText(route.getOrigTc());   // 始发站中文
        holder.origEn.setText(route.getOrigEn());   // 始发站英文
        holder.destTc.setText(route.getDestTc());   // 目的地中文
        holder.destEn.setText(route.getDestEn());   // 目的地英文
    }

    @Override
    public int getItemCount() {
        return busRoutes == null ? 0 : busRoutes.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvRouteNumber, origTc, destTc, origEn, destEn;

        ViewHolder(View view) {
            super(view);
            tvRouteNumber = view.findViewById(R.id.tvRouteNumber);
            origTc = view.findViewById(R.id.orig_tc);
            destTc = view.findViewById(R.id.dest_tc);
            origEn = view.findViewById(R.id.orig_en);
            destEn = view.findViewById(R.id.dest_en);
        }
    }
}