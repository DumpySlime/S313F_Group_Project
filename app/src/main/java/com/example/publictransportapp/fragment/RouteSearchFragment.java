package com.example.publictransportapp.fragment;


import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.publictransportapp.R;
import com.example.publictransportapp.model.Stop;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RouteSearchFragment extends Fragment {

    private AutoCompleteTextView inputStart;
    private AutoCompleteTextView inputEnd;
    private Button buttonFindPath;
    private OnRouteSearchListener listener;

    public interface OnRouteSearchListener {
        void onSearchRoute(String startName, String endName);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Ensure the listener is from ShortestPathFragment
        Fragment parentFragment = getParentFragment();
        if (parentFragment instanceof OnRouteSearchListener) {
            listener = (OnRouteSearchListener) parentFragment;
        } else {
            throw new RuntimeException(parentFragment + " must implement OnRouteSearchListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_route_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        inputStart = view.findViewById(R.id.inputStart);
        inputEnd = view.findViewById(R.id.inputEnd);
        buttonFindPath = view.findViewById(R.id.buttonFindPath);

        buttonFindPath.setOnClickListener(v -> {
            String startName = inputStart.getText().toString().trim();
            String endName = inputEnd.getText().toString().trim();

            if (validateInput(startName, endName)) {
                if (listener != null) {
                    listener.onSearchRoute(startName, endName);
                }
            }
        });
    }

    private boolean validateInput(String start, String end) {
        if (start.isEmpty() || end.isEmpty()) {
            Toast.makeText(getContext(), getString(R.string.select_start_end), Toast.LENGTH_SHORT).show();
            return false;
        }

        if (start.equalsIgnoreCase(end)) {
            Toast.makeText(getContext(), getString(R.string.same_start_end), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public void setupAutocomplete(List<Stop> stops) {
        if (getContext() == null || stops == null) return;

        List<String> stopNames = new ArrayList<>();
        for (Stop stop : stops) {
            stopNames.add(stop.getNameEn());
        }
        Collections.sort(stopNames);
        Log.d("RouteSearchFragment", "Stop Names: " + stopNames.toString());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                getContext(), android.R.layout.simple_dropdown_item_1line, stopNames);
        inputStart.setAdapter(adapter);
        inputEnd.setAdapter(adapter);

        buttonFindPath.post(() -> buttonFindPath.setEnabled(true));
    }
}