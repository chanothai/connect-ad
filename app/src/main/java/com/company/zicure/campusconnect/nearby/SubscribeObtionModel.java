package com.company.zicure.campusconnect.nearby;

import com.google.android.gms.nearby.messages.SubscribeOptions;

/**
 * Created by macintosh on 27/2/18.
 */

public class SubscribeObtionModel {
    private SubscribeOptions options;
    private static SubscribeObtionModel me = null;

    public SubscribeObtionModel(){

    }

    public static SubscribeObtionModel getInstance(){
        if (me == null){
            me = new SubscribeObtionModel();
        }

        return me;
    }

    public SubscribeOptions getOptions() {
        return options;
    }

    public void setOptions(SubscribeOptions options) {
        this.options = options;
    }
}
