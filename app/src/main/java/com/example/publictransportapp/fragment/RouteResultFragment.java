package com.example.publictransportapp.fragment;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.publictransportapp.R;

public class RouteResultFragment extends Fragment {

    private TextView resultText;
    private Button buttonBack;
    private OnBackToSearchListener listener;

    public interface OnBackToSearchListener {
        void onBackToSearch();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Fragment parentFragment = getParentFragment();
        if (parentFragment instanceof OnBackToSearchListener) {
            listener = (OnBackToSearchListener) parentFragment;
        } else {
            throw new RuntimeException(parentFragment + " must implement OnBackToSearchListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_route_result, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        resultText = view.findViewById(R.id.resultText);
        buttonBack = view.findViewById(R.id.buttonBack);

        buttonBack.setOnClickListener(v -> {
            if (listener != null) {
                listener.onBackToSearch();
            }
        });
    }

    public void setResultText(String result) {
        if (resultText != null) {
            resultText.setText(result);
        }
    }
}