package com.example.publictransportapp;

import com.example.publictransportapp.model.RouteEtaModel;

public interface EtaCallback {
    void onEtaReceived(RouteEtaModel routeEtaModel);
    void onError(String error);
}
