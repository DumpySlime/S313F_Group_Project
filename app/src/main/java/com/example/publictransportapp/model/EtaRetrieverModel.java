package com.example.publictransportapp.model;

public class EtaRetrieverModel {
    private int[] eta;

    public EtaRetrieverModel(int[] eta) {
        this.eta = eta;
    }

    public EtaRetrieverModel(){
        this.eta = new int[] {-1,-1,-1};
    }

    public int[] getEta() {
        return eta;
    }

    public int getEta(int pos) {
        return eta[pos];
    }

    public void setEta(int[] eta) {
        this.eta = eta;
    }

    public void setEta(int eta, int pos) {
        this.eta[pos] = eta;
    }

}
