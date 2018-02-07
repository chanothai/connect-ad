package com.company.zicure.campusconnect.activity;

import android.os.Build;
import android.provider.Settings;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.company.zicure.campusconnect.R;
import com.company.zicure.campusconnect.fragment.AddFriendFragment;
import com.company.zicure.campusconnect.fragment.AppMenuFragment;
import com.company.zicure.campusconnect.network.ClientHttp;
import com.squareup.otto.Subscribe;

import gallery.zicure.company.com.modellibrary.common.BaseActivity;
import gallery.zicure.company.com.modellibrary.models.bloc.RequestCheckInWork;
import gallery.zicure.company.com.modellibrary.models.bloc.ResponseCheckinWork;
import gallery.zicure.company.com.modellibrary.utilize.EventBusCart;
import gallery.zicure.company.com.modellibrary.utilize.ModelCart;
import gallery.zicure.company.com.modellibrary.utilize.ToolbarManager;
import gallery.zicure.company.com.modellibrary.utilize.VariableConnect;

public class BlocContentActivity extends BaseActivity {
    /** Make: View **/
    private FrameLayout frameLayout = null;
    private Toolbar toolbar = null;
    private TextView textTitle = null;
    private WebView webView = null;

    /** Make: Properties **/
    private String titleBloc = null;
    private String urlBloc = null;
    private boolean statusCheckIn = false;

    private AppMenuFragment appMenuFragment = null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bloc_content);
        EventBusCart.getInstance().getEventBus().register(this);
        bindView();
        iniBundle();
        setToolbar();

        if (savedInstanceState == null) {
            checkIniFragment();
        }
    }

    private void bindView(){
        frameLayout = (FrameLayout) findViewById(R.id.container_bloc);
        toolbar = (Toolbar) findViewById(R.id.toolbar_bloc);
        textTitle = (TextView) toolbar.findViewById(R.id.text_title);
    }

    private void iniBundle(){
        try{
            Bundle bundle = getIntent().getExtras();
            titleBloc = bundle.getString(VariableConnect.TITLE_CATEGORY);
            urlBloc = bundle.getString(VariableConnect.PATH_BLOC);
            statusCheckIn = bundle.getBoolean("status_check_in");
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    private void setToolbar(){
        if (Build.VERSION.SDK_INT >= 21) {
            ToolbarManager manager = new ToolbarManager(this);
            manager.setToolbar(toolbar, textTitle, getDrawable(R.drawable.back_screen), titleBloc);

            if (urlBloc.isEmpty()){
                CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) frameLayout.getLayoutParams();
                params.setBehavior(new AppBarLayout.ScrollingViewBehavior());
                frameLayout.requestLayout();
            }
        }
    }

    private void checkIniFragment(){
        if (!urlBloc.isEmpty()){
            iniFragmentBloc();
        }else{
            if (statusCheckIn){
                requestCheckinWork();
            }else{
                initFragmentAddFriend();
            }
        }
    }

    private void requestCheckinWork() {
        String deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        ModelCart.getInstance().getRequestCheckInWork().setDeviceId(deviceId);
        ModelCart.getInstance().getRequestCheckInWork().setToken(ModelCart.getInstance().getKeyModel().getToken());

        RequestCheckInWork request = new RequestCheckInWork();
        request.setData(ModelCart.getInstance().getRequestCheckInWork());
        ClientHttp.getInstance(this).requestCheckInWork(request);
    }

    private void iniFragmentBloc(){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container_bloc, AppMenuFragment.newInstance(urlBloc, true), VariableConnect.appMenuFragmentKey);
        transaction.commit();
    }

    private void initFragmentAddFriend(){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container_bloc, AddFriendFragment.newInstance("",""), "AddFriendFragment");
        transaction.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{
                try{
                    webView = AppMenuFragment.webView;
                    if (webView != null && webView.canGoBack()){
                        webView.goBack();
                    }else{
                        finish();
                        overridePendingTransition(R.anim.anim_scale_in, R.anim.anim_slide_out_right);
                    }
                }catch(NullPointerException e){
                    finish();
                    overridePendingTransition(R.anim.anim_scale_in, R.anim.anim_slide_out_right);
                }
                break;
            }
            case R.id.action_home_screen :
                finish();
                break;

            default: {
                break;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_bloc_content, menu);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBusCart.getInstance().getEventBus().unregister(this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            webView = AppMenuFragment.webView;
            if (webView != null){
                if (webView.canGoBack()){
                    webView.goBack();
                }else{
                    finish();
                    overridePendingTransition(R.anim.anim_scale_in, R.anim.anim_slide_out_right);
                }
            }

            finish();
            overridePendingTransition(R.anim.anim_scale_in, R.anim.anim_slide_out_right);
        }
        return super.onKeyDown(keyCode, event);
    }

    private AppMenuFragment getFragmentAppMenu(){
        FragmentManager fm = getSupportFragmentManager();
        AppMenuFragment fragment = (AppMenuFragment) fm.findFragmentByTag(VariableConnect.appMenuFragmentKey);
        return fragment;
    }

    @Subscribe
    public void onEventResponseCheckinWork(ResponseCheckinWork response) {
        if (response.getResult().getSuccess().equalsIgnoreCase("OK")) {
            urlBloc = response.getResult().getData().getUrl();
            iniFragmentBloc();
        }else{
            Toast.makeText(this, response.getResult().getError(), Toast.LENGTH_SHORT).show();
        }
    }

    /****************************************************/

    private void resumeCamera() {
        FragmentManager fm = getSupportFragmentManager();
        AddFriendFragment fragment = (AddFriendFragment) fm.findFragmentByTag("AddFriendFragment");
        fragment.resumeCamera();
    }
}
