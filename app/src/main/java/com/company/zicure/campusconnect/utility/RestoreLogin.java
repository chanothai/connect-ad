package com.company.zicure.campusconnect.utility;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.company.zicure.campusconnect.R;
import com.company.zicure.campusconnect.activity.LoginActivity;
import com.company.zicure.campusconnect.activity.MainMenuActivity;

import gallery.zicure.company.com.modellibrary.utilize.ModelCart;
import gallery.zicure.company.com.modellibrary.utilize.VariableConnect;

/**
 * Created by 4GRYZ52 on 3/22/2017.
 */

public class RestoreLogin {
    private static RestoreLogin me = null;
    private Context context = null;
    private SharedPreferences pref = null;

    public RestoreLogin(Context context){
        this.context = context;
        pref = context.getSharedPreferences(VariableConnect.keyFile, Context.MODE_PRIVATE);
    }

    public static RestoreLogin getInstance(Context context){
        if (me == null){
            me = new RestoreLogin(context);
        }
        return me;
    }

    public String getRestoreToken(){
        String token = pref.getString(context.getString(R.string.token_login), null);
        return token;
    }

    public String getURL(){
        String user = pref.getString("web_url", null);
        return user;
    }

    public String getSubscribe(){
        String key = pref.getString("subscribe_noti", null);
        return key;
    }

    public void clearAllData(){
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.apply();

        StackURLController.getInstance().clearAllStackURL();
    }
}
