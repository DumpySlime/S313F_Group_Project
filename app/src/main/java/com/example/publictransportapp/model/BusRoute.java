package com.example.publictransportapp.model;


public class BusRoute {
    private String routeNumber;
    private String origTc;
    private String destTc;
    private String origEn;
    private String destEn;

    public BusRoute(String routeNumber,
                    String origTc, String destTc,
                    String origEn, String destEn) {
        this.routeNumber = routeNumber;
        this.origTc = origTc;
        this.destTc = destTc;
        this.origEn = origEn;
        this.destEn = destEn;
    }


    public String getRouteNumber() { return routeNumber; }
    public String getOrigTc() { return origTc; }
    public String getDestTc() { return destTc; }
    public String getOrigEn() { return origEn; }
    public String getDestEn() { return destEn; }
}
