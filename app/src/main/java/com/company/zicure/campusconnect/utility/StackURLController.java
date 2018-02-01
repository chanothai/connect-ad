package com.company.zicure.campusconnect.utility;

import java.util.ArrayList;

/**
 * Created by macintosh on 1/2/18.
 */

public class StackURLController {
    private static StackURLController me;
    private ArrayList<String> stackURL;

    public StackURLController() {
        stackURL = new ArrayList<>();
    }

    public static StackURLController getInstance(){
        if (me == null) {
            me = new StackURLController();
        }

        return me;
    }

    public void resetStackUrl(String mainURL, String subURL){
        stackURL.clear();
        stackURL.add(mainURL);
        stackURL.add(subURL);
    }

    public ArrayList<String> getStackURL() {
        return stackURL;
    }

    public void setStackURL(ArrayList<String> stackURL) {
        this.stackURL = stackURL;
    }
}
