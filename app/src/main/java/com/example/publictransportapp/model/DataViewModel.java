package com.example.publictransportapp.model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

public class DataViewModel extends ViewModel {
    private final MutableLiveData<ArrayList<RouteListModel>> routeList;
    private final MutableLiveData<ArrayList<StopListModel>> stopList;
    private final MutableLiveData<ArrayList<RouteStopListModel>> routeStopList;

    public DataViewModel() {
        routeList = new MutableLiveData<>();
        stopList = new MutableLiveData<>();
        routeStopList = new MutableLiveData<>();
    }

    public void setRouteList(ArrayList<RouteListModel> routesData) {
        routeList.setValue(routesData);
    }

    public LiveData<ArrayList<RouteListModel>> getRouteList() {
        return routeList;
    }

    public void setStopList(ArrayList<StopListModel> stopsData) {
        stopList.setValue(stopsData);
    }

    public LiveData<ArrayList<StopListModel>> getStopList() {
        return stopList;
    }

    public void setRouteStopList(ArrayList<RouteStopListModel> routeStopsData) {
        routeStopList.setValue(routeStopsData);
    }

    public LiveData<ArrayList<RouteStopListModel>> getRouteStopList() {
        return routeStopList;
    }
}
