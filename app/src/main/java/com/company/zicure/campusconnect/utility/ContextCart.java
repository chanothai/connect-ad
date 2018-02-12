package com.company.zicure.campusconnect.utility;

import android.content.Context;

/**
 * Created by macintosh on 12/2/18.
 */

public class ContextCart {
    private static ContextCart me = null;
    private Context context;

    public static ContextCart getInstance() {
        if (me == null) {
            me = new ContextCart();
        }

        return me;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
