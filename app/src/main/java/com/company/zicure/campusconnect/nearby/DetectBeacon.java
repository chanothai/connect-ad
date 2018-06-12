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
    private static DetectBeacon me;
    private Context context;
    private static String TAG = "NearbyScan";
    private String body, result;

    private BeaconModel model = null;
    private ArrayList<BeaconModel> stackBeacon;

    public DetectBeacon(Context context) {
        this.context = context;
        stackBeacon = new ArrayList<>();
    }

    public static DetectBeacon getInstance(Context context) {
        if (me == null){
            me = new DetectBeacon(context);
        }
        return me;
    }

    public ArrayList<BeaconModel> getStackBeacon() {
        return stackBeacon;
    }

    public void setStackBeacon(ArrayList<BeaconModel> stackBeacon){
        this.stackBeacon = stackBeacon;
    }

    @Override
    public void onFound(Message message) {
        body = new String(message.getContent());
        result = new String(body.getBytes(Charset.forName("UTF-8")));

        model = new BeaconModel();
        model.setDevice(result);
        stackBeacon.add(model);

        Log.d(TAG + " Found", new Gson().toJson(stackBeacon));
    }

    @Override
    public void onLost(Message message) {
        body = new String(message.getContent());
        result = new String(body.getBytes(Charset.forName("UTF-8")));

        if (stackBeacon != null) {
            for (int i = 0; i < stackBeacon.size(); i++){
                if (result.equalsIgnoreCase(stackBeacon.get(i).getDevice())){
                    stackBeacon.remove(i);
                }
            }
        }
        Log.d(TAG + " LOST", new Gson().toJson(stackBeacon));
    }

    @Override
    public void onDistanceChanged(Message message, Distance distance) {
        String content = new String(message.getContent());
        String device = new String(content.getBytes(Charset.forName("UTF-8")));

        for (int i = 0; i < stackBeacon.size(); i++){
            if (device.equalsIgnoreCase(stackBeacon.get(i).getDevice())){
                stackBeacon.get(i).setDistance(Double.toString(distance.getMeters()));
            }
        }
    }

    @Override
    public void onBleSignalChanged(Message message, BleSignal bleSignal) {
        String content = new String(message.getContent());
        String device = new String(content.getBytes(Charset.forName("UTF-8")));

        for (int i = 0; i < stackBeacon.size(); i++){
            if (device.equalsIgnoreCase(stackBeacon.get(i).getDevice())){
                stackBeacon.get(i).setRssi(Integer.toString(bleSignal.getRssi()));
            }
        }
    }
}
