package com.company.zicure.campusconnect.utility;

import android.Manifest;
import android.app.Activity;

/**
 * Created by 4GRYZ52 on 2/1/2017.
 */

public class PermissionRequest {
    private Activity context = null;
    private PermissionManager manager = null;


    public PermissionRequest(Activity context){
        this.context = context;
        manager = new PermissionManager(context);
    }

    public boolean requestReadStorage(){
        String[] arrPermission = {Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.CAMERA,
                Manifest.permission.CALL_PHONE};

        if (!manager.checkPermission(arrPermission
                ,PermissionKeyNumber.getInstance().getPermissionReadStorageKey())){
            return false;
        }
        return true;
    }
}
