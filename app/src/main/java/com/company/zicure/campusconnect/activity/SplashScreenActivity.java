package com.company.zicure.campusconnect.activity;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.company.zicure.campusconnect.R;
import com.company.zicure.campusconnect.utility.PermissionKeyNumber;
import com.company.zicure.campusconnect.utility.PermissionRequest;
import com.company.zicure.campusconnect.utility.SharedPreference;

import com.company.zicure.campusconnect.common.BaseActivity;
import com.company.zicure.campusconnect.utility.EventBusCart;

public class SplashScreenActivity extends BaseActivity implements Animator.AnimatorListener{

    private PermissionRequest permissionRequest = null;

    private String currentToken = null, currentURL = null,currentSubscribe = null, value = null;

    ImageView imgLogo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        EventBusCart.getInstance().getEventBus().register(this);

        imgLogo = findViewById(R.id.img_logo);
        permissionRequest = new PermissionRequest(this);

        if (getIntent().getExtras() != null) {
            value = getIntent().getExtras().getString("badge", null);
            if (value != null) {
                Log.d("Notification_Object", value);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!permissionRequest.requestReadStorage()){
            if (!permissionRequest.requestReadPhoneState()){
                animationFadeOut();
            }
        }
    }

    public void animationFadeOut() {
        AnimatorSet animSet = new AnimatorSet();
        ObjectAnimator animatorFadeIn = ObjectAnimator.ofFloat(imgLogo, View.ALPHA, 1f);
        ObjectAnimator animatorFadeOut = ObjectAnimator.ofFloat(imgLogo, View.ALPHA, 0f);
        animSet.playSequentially(animatorFadeIn, animatorFadeOut);
        animSet.setDuration(1000);
        animSet.addListener(this);
        animSet.start();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (PermissionKeyNumber.getInstance().getPermissionReadStorageKey() == requestCode){
            if (grantResults[0] != -1){
                permissionRequest.requestReadPhoneState();
            }
        }
        else if (requestCode == 200) {
            if (grantResults[0] != -1) {
                animationFadeOut();
            }
        }
    }

    @Override

    public void onAnimationStart(Animator animation) {

    }

    @Override
    public void onAnimationEnd(Animator animation) {
        checkLogin();
    }

    private void checkLogin(){
        currentToken = SharedPreference.getInstance(this).getRestoreToken();
        currentURL = SharedPreference.getInstance(this).getURL();
        currentSubscribe = SharedPreference.getInstance(this).getSubscribe();

        if (currentSubscribe != null && currentToken != null && currentURL != null){
            if (value != null) {
                Bundle bundle = new Bundle();
                bundle.putString("badge", value);
                openActivity(MainMenuActivity.class,bundle, true);
            }else{
                openActivity(MainMenuActivity.class, true);
            }
        }else{
            openActivity(LoginActivity.class, true);
        }
    }

    @Override
    public void onAnimationCancel(Animator animation) {

    }

    @Override
    public void onAnimationRepeat(Animator animation) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBusCart.getInstance().getEventBus().unregister(this);
    }
}
