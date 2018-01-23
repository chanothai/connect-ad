package com.company.zicure.campusconnect.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.webkit.WebView;

import com.company.zicure.campusconnect.R;
import com.company.zicure.campusconnect.fragment.AppMenuFragment;

import java.util.ArrayList;

import gallery.zicure.company.com.modellibrary.common.BaseActivity;
import gallery.zicure.company.com.modellibrary.utilize.VariableConnect;

public class LoginActivity extends BaseActivity {
    /** Make: View **/

    /** Make: Properties **/
    private SharedPreferences sharedPref = null;

    private String url = "http://connect06.pakgon.com/core/";
//    private String url = "file:///android_asset/index.html";
    //file:///android_asset/webview.html

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (savedInstanceState == null){
            bindView();
            sharedPref = getSharedPreferences(VariableConnect.keyFile, Context.MODE_PRIVATE);
        }

    }

    private void bindView(){
        if (MainMenuActivity.STACK_URL == null){
            MainMenuActivity.STACK_URL = new ArrayList<>();
            MainMenuActivity.STACK_URL.add(url);
        }

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, AppMenuFragment.newInstance(url),VariableConnect.appMenuFragmentKey);
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

    private AppMenuFragment getFragmentAppMenu(){
        FragmentManager fm = getSupportFragmentManager();
        AppMenuFragment fragment = (AppMenuFragment) fm.findFragmentByTag(VariableConnect.appMenuFragmentKey);
        return fragment;
    }

    @Override
    public void onBackPressed() {
        if (MainMenuActivity.STACK_URL.size() > 1) {
            int countStack = MainMenuActivity.STACK_URL.size() - 2;
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.container, AppMenuFragment.newInstance(MainMenuActivity.STACK_URL.get(countStack)), VariableConnect.appMenuFragmentKey);
            transaction.commit();

            MainMenuActivity.STACK_URL.remove(countStack + 1);
        }else{
            super.onBackPressed();
        }

//        if (AppMenuFragment.webView != null){
//            if (AppMenuFragment.webView.canGoBack()){
//                AppMenuFragment.webView.goBack();
//            }else{
//                super.onBackPressed();
//            }
//        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        MainMenuActivity.STACK_URL = null;
    }
}
