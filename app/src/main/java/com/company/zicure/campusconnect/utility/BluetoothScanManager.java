package com.company.zicure.campusconnect.utility;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.util.Log;

import gallery.zicure.company.com.modellibrary.utilize.ModelCart;

/**
 * Created by Pakgon on 9/18/2017 AD.
 */

public class BluetoothScanManager implements BluetoothAdapter.LeScanCallback {
    private static final String LOG_TAG = "BluetoothScan";
    private Context context = null;
    private OnListenerScanning onListenerScanning = null;
    private String beacon = null;

    public BluetoothScanManager(Context context, OnListenerScanning onListenerScanning){
        this.context = context;
        this.onListenerScanning = onListenerScanning;
        beacon = null;
    }

    @Override
    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
        int startByte = 2;
        boolean patternFound = false;
        while (startByte <= 5)
        {
            if (    ((int) scanRecord[startByte + 2] & 0xff) == 0x02 && //Identifies an iBeacon
                    ((int) scanRecord[startByte + 3] & 0xff) == 0x15)
            { //Identifies correct data length
                patternFound = true;
                break;
            }
            startByte++;
        }

        if (patternFound)
        {
            //Convert to hex String
            byte[] uuidBytes = new byte[16];
            System.arraycopy(scanRecord, startByte + 4, uuidBytes, 0, 16);
            String hexString = bytesToHex(uuidBytes);

            //UUID detection
            final String uuid =  hexString.substring(0,8) + "-" +
                    hexString.substring(8,12) + "-" +
                    hexString.substring(12,16) + "-" +
                    hexString.substring(16,20) + "-" +
                    hexString.substring(20,32);

            // major
            final int major = (scanRecord[startByte + 20] & 0xff) * 0x100 + (scanRecord[startByte + 21] & 0xff);

            // minor
            final int minor = (scanRecord[startByte + 22] & 0xff) * 0x100 + (scanRecord[startByte + 23] & 0xff);

            Log.d("UUID:", uuid);
            if (uuid.equalsIgnoreCase("2C29C134-61D7-46E2-821A-DC65DFA52BDD")) {
                if (beacon == null){
                    beacon = uuid;
                    onListenerScanning.onResponseScan(beacon);
                }
            }else{
                ModelCart.getInstance().getRequestCheckInWork().setUuid("");
            }
        }
    }

    static final char[] hexArray = "0123456789ABCDEF".toCharArray();
    private static String bytesToHex(byte[] bytes)
    {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ )
        {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public interface OnListenerScanning {
        void onResponseScan(String uuid);
    }
}
