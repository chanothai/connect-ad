package com.company.zicure.campusconnect.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.company.zicure.campusconnect.R;
import com.company.zicure.campusconnect.fragment.AppMenuFragment;
import com.company.zicure.campusconnect.network.ClientHttp;
import com.company.zicure.campusconnect.utility.StackURLController;

import com.company.zicure.campusconnect.common.BaseActivity;
import com.company.zicure.campusconnect.utility.VariableConnect;

public class LoginActivity extends BaseActivity {
    /** Make: View **/

    /** Make: Properties **/
    private SharedPreferences sharedPref = null;

    private String url = ClientHttp.URL_SIGNIN;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if (savedInstanceState == null){
            bindView();
            sharedPref = getSharedPreferences(VariableConnect.keyFile, Context.MODE_PRIVATE);
        }
    }

    private void bindView(){
        StackURLController.getInstance().getStackURL().add(url);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, AppMenuFragment.newInstance(url, true),VariableConnect.appMenuFragmentKey);
        transaction.commit();
    }

    public void store(String token, String url, String subscribeNoti){
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("token", token);
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
        if (StackURLController.getInstance().getStackURL().size() > 1) {
            int countStack = StackURLController.getInstance().getStackURL().size() - 2;
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.container, AppMenuFragment.newInstance(StackURLController.getInstance().getStackURL().get(countStack), false), VariableConnect.appMenuFragmentKey);
            transaction.commit();

            StackURLController.getInstance().getStackURL().remove(countStack + 1);
        }else{
            super.onBackPressed();
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
