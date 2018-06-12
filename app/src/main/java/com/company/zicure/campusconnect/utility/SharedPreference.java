package com.company.zicure.campusconnect.utility;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by 4GRYZ52 on 3/22/2017.
 */

public class SharedPreference {
    private static SharedPreference me = null;
    private Context context = null;
    private SharedPreferences pref = null;

    public SharedPreference(Context context){
        this.context = context;
        pref = context.getSharedPreferences(VariableConnect.keyFile, Context.MODE_PRIVATE);
    }

    public static SharedPreference getInstance(Context context){
        if (me == null){
            me = new SharedPreference(context);
        }
        return me;
    }

    public String getRestoreToken(){
        String token = pref.getString("token", null);
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
