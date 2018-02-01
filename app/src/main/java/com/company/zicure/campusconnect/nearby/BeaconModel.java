package com.company.zicure.campusconnect.nearby;

/**
 * Created by macintosh on 24/1/18.
 */

public class BeaconModel {
    private String device;
    private String distance;
    private String rssi;

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

    public String getRssi() {
        return rssi;
    }

    public void setRssi(String rssi) {
        this.rssi = rssi;
    }
}
