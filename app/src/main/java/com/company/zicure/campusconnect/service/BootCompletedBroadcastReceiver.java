package com.company.zicure.campusconnect.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by macintosh on 31/1/18.
 */

public class BootCompletedBroadcastReceiver extends BroadcastReceiver{

    private String TAG = "Android-BroadcastReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent serviceIntent = new Intent(context, MessagingService.class);
        context.startService(serviceIntent);
        Log.i(TAG, "Start service");
    }
}
