package com.company.zicure.campusconnect.service;

import android.content.Context;
import android.content.SharedPreferences;

import gallery.zicure.company.com.modellibrary.utilize.VariableConnect;

/**
 * Created by macintosh on 31/1/18.
 */

public class BadgeController {
    private Context context = null;
    private static BadgeController me = null;
    private int countBadge = 0;
    private SharedPreferences preferences = null;

    private static String BADGE_KEY = "badge";

    private BadgeController(Context context){
        this.context = context;
        preferences = context.getSharedPreferences(VariableConnect.keyFile, Context.MODE_PRIVATE);
        countBadge = preferences.getInt("badge", 0);
    }

    public static BadgeController getInstance(Context context){
        if (me == null) {
            me = new BadgeController(context);
        }
        return me;
    }


    public int getCountBadge() {
        return countBadge;
    }

    public void setCountBadge(int countBadge) {
        this.countBadge = countBadge;
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(BADGE_KEY, countBadge);
        editor.apply();
    }
}
