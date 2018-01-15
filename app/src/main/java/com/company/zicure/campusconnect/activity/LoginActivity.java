package com.company.zicure.campusconnect.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.company.zicure.campusconnect.R;
import com.company.zicure.campusconnect.fragment.AppMenuFragment;

import gallery.zicure.company.com.modellibrary.common.BaseActivity;
import gallery.zicure.company.com.modellibrary.utilize.VariableConnect;

public class LoginActivity extends BaseActivity {
    /** Make: View **/

    /** Make: Properties **/

    private String strUser = "", strPass = "";
    private SharedPreferences sharedPref = null;

    private String url = "http://connect06.pakgon.com/core/";
//    private String url = "file:///android_asset/index.html";
    //file:///android_asset/webview.html

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        bindView();

        if (savedInstanceState == null){
            sharedPref = getSharedPreferences(VariableConnect.keyFile, Context.MODE_PRIVATE);
        }
    }

    private void bindView(){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.container, AppMenuFragment.newInstance(url), "WebView_Login");
        transaction.commit();
    }

    public void store(String token, String url, String subscribeNoti){
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.token_login), token);
        editor.putString("web_url", url);
        editor.putString("subscribe_noti", subscribeNoti);
        editor.apply();

        openActivity(MainMenuActivity.class, true);
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
