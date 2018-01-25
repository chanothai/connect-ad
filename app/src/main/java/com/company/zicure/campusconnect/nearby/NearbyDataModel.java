package com.company.zicure.campusconnect.nearby;

/**
 * Created by macintosh on 24/1/18.
 */

public class NearbyDataModel {
    private BeaconModel beaconModel;
    private String token;

    public BeaconModel getBeaconModel() {
        return beaconModel;
    }

    public void setBeaconModel(BeaconModel beaconModel) {
        this.beaconModel = beaconModel;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
