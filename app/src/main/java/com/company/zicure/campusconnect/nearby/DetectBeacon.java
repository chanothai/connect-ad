package com.company.zicure.campusconnect.nearby;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.nearby.messages.BleSignal;
import com.google.android.gms.nearby.messages.Distance;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;
import com.google.gson.Gson;

import java.nio.charset.Charset;
import java.util.ArrayList;

/**
 * Created by macintosh on 24/1/18.
 */

public class DetectBeacon extends MessageListener {
    private Context context;
    private static String TAG = "NearbyScan";
    private String body, result;

    private BeaconModel model = null;
    public static ArrayList<BeaconModel> STACK_BEACON;

    public DetectBeacon(Context context) {
        this.context = context;
    }

    @Override
    public void onFound(Message message) {
        super.onFound(message);
        body = new String(message.getContent());
        result = new String(body.getBytes(Charset.forName("UTF-8")));

        if (STACK_BEACON == null) {
            STACK_BEACON = new ArrayList<>();
        }

        model = new BeaconModel();
        model.setDevice(result);
        model.setDistance("");
        STACK_BEACON.add(model);

        Log.d(TAG, new Gson().toJson(STACK_BEACON));
    }

    @Override
    public void onLost(Message message) {
        super.onLost(message);
        body = new String(message.getContent());
        result = new String(body.getBytes(Charset.forName("UTF-8")));

        for (int i = 0; i < STACK_BEACON.size(); i++){
            if (result.equalsIgnoreCase(STACK_BEACON.get(i).getDevice())){
                STACK_BEACON.remove(i);
            }
        }
        Log.d(TAG + " LOST", new Gson().toJson(STACK_BEACON));
    }

    @Override
    public void onDistanceChanged(Message message, Distance distance) {
        super.onDistanceChanged(message, distance);
        String content = new String(message.getContent());
        String device = new String(content.getBytes(Charset.forName("UTF-8")));

        for (int i = 0; i < STACK_BEACON.size(); i++){
            if (device.equalsIgnoreCase(STACK_BEACON.get(i).getDevice())){
                STACK_BEACON.get(i).setDistance(Double.toString(distance.getMeters()));
            }
        }
    }

    @Override
    public void onBleSignalChanged(Message message, BleSignal bleSignal) {
        super.onBleSignalChanged(message, bleSignal);
        String content = new String(message.getContent());
        String device = new String(content.getBytes(Charset.forName("UTF-8")));

        for (int i = 0; i < STACK_BEACON.size(); i++){
            if (device.equalsIgnoreCase(STACK_BEACON.get(i).getDevice())){
                STACK_BEACON.get(i).setRsi(Integer.toString(bleSignal.getRssi()));
            }
        }
    }
}
