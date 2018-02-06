package com.company.zicure.campusconnect.utility;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * Created by 4GRYZ52 on 10/22/2016.
 */
public class PermissionManager {
    private Activity context = null;

    public PermissionManager(Activity context){
        this.context = context;
    }

    public boolean checkPermission(String[] permission, int myPermission){
        if (ContextCompat.checkSelfPermission(context, permission[0]) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(context, new String[]{permission[0], permission[1], permission[2], permission[3]}, myPermission);
            return true; // true is request permission
        }
        return false;
    }


    public boolean checkReadPhoneState(String permission, int number){
        if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(context, new String[]{permission}, number);
            return true; // true is request permission
        }
        return false;
    }
}
