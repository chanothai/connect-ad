package com.company.zicure.campusconnect.nearby;

/**
 * Created by macintosh on 24/1/18.
 */

public class BeaconModel {
    private String device;
    private String distance;
    private String rsi;

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getRsi() {
        return rsi;
    }

    public void setRsi(String rsi) {
        this.rsi = rsi;
    }
}
