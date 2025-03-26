package com.example.publictransportapp.model;

import com.android.volley.toolbox.JsonObjectRequest;

public class EtaRetrieverModel {
    private int[] eta;
    private JsonObjectRequest jsonObjectRequest;

    public EtaRetrieverModel(int[] eta, JsonObjectRequest jsonObjectRequest) {
        this.eta = eta;
        this.jsonObjectRequest = jsonObjectRequest;
    }

    public EtaRetrieverModel(){
        this.eta = new int[] {-1,-1,-1};
        this.jsonObjectRequest = null;
    }

    public int[] getEta() {
        return eta;
    }

    public int getEta(int pos) {
        return eta[pos - 1];
    }

    public JsonObjectRequest getJsonObjectRequest() {
        return jsonObjectRequest;
    }
    public void setEta(int[] eta) {
        this.eta = eta;
    }

    public void setEta(int eta, int pos) {
        this.eta[pos] = eta;
    }

    public void setJsonObjectRequest(JsonObjectRequest jsonObjectRequest) {
        this.jsonObjectRequest = jsonObjectRequest;
    }
}
