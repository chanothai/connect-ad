package com.company.zicure.campusconnect.activity;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Handler;
import android.os.PersistableBundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.VelocityTrackerCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.company.zicure.campusconnect.R;
import com.company.zicure.campusconnect.adapter.MenuCategoryAdapter;
import com.company.zicure.campusconnect.customView.BottomNavigationViewHelper;
import com.company.zicure.campusconnect.fragment.AppMenuFragment;

import com.company.zicure.campusconnect.modelview.Item;
import com.company.zicure.campusconnect.nearby.BeaconModel;
import com.company.zicure.campusconnect.nearby.DetectBeacon;
import com.company.zicure.campusconnect.network.ClientHttp;
import com.company.zicure.campusconnect.network.request.ProfileRequest;
import com.company.zicure.campusconnect.service.BadgeController;
import com.company.zicure.campusconnect.service.MessagingService;
import com.company.zicure.campusconnect.utility.BluetoothScanManager;
import com.company.zicure.campusconnect.utility.PermissionKeyNumber;
import com.company.zicure.campusconnect.utility.PermissionRequest;
import com.company.zicure.campusconnect.utility.StackURLController;
import com.company.zicure.campusconnect.view.viewgroup.FlyOutContainer;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageFilter;
import com.google.android.gms.nearby.messages.MessagesOptions;
import com.google.android.gms.nearby.messages.NearbyPermissions;
import com.google.android.gms.nearby.messages.Strategy;
import com.google.android.gms.nearby.messages.SubscribeOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.joooonho.SelectableRoundedImageView;
import com.squareup.otto.Subscribe;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import gallery.zicure.company.com.modellibrary.common.BaseActivity;
import gallery.zicure.company.com.modellibrary.models.beacon.BeaconResponse;
import gallery.zicure.company.com.modellibrary.models.bloc.RequestCheckInWork;
import gallery.zicure.company.com.modellibrary.models.profile.ProfileResponse;
import gallery.zicure.company.com.modellibrary.utilize.EventBusCart;
import gallery.zicure.company.com.modellibrary.utilize.ModelCart;
import gallery.zicure.company.com.modellibrary.utilize.ResizeScreen;
import gallery.zicure.company.com.modellibrary.utilize.ToolbarManager;
import gallery.zicure.company.com.modellibrary.utilize.VariableConnect;
import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;
import io.nlopez.smartlocation.location.config.LocationAccuracy;
import io.nlopez.smartlocation.location.config.LocationParams;
import me.leolin.shortcutbadger.ShortcutBadger;

public class MainMenuActivity extends BaseActivity implements OnLocationUpdatedListener, BottomNavigationView.OnNavigationItemSelectedListener, BottomNavigationView.OnNavigationItemReselectedListener {

    /** Make: View **/
    RelativeLayout linearLayout;
    LinearLayout layoutMenu;

    //Bottom navigation
    private BottomNavigationView bottomBar = null;

    //toolbar
    Toolbar toolbarMenu;

    //list view slide menu
    RecyclerView listSlideMenu;
    FrameLayout layoutGhost;
    FrameLayout controlSlide;
    SelectableRoundedImageView imgProfile, btnScanQRCode;
    TextView profileName;
    TextView profileAddress;

    //list menu
    private ArrayList<Item> arrMenu = null;
    RelativeLayout childHeaderDrawer;
    RelativeLayout headerDrawer;

    /** Make: properties **/
    //view layout
    private FlyOutContainer root;
    private int widthScreenMenu;
    int haftScreen = 0;
    private VelocityTracker velocityTracker = null; // get speed for touch

    private String currentToken = null;

    private String geoAddress = null, subscribe, url;

    //Nearby

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBusCart.getInstance().getEventBus().register(this);
        root = (FlyOutContainer) getLayoutInflater().inflate(R.layout.activity_main_menu, null);

        if (savedInstanceState == null) {
            setContentView(root);
            bindView();
            setToolbar();
            setOnTouchView();
            initParameter();

            showLoadingDialog();
            String language = Locale.getDefault().getDisplayLanguage().toString();
            ModelCart.getInstance().getKeyModel().setLanguage(language);
            ProfileRequest profileRequest = new ProfileRequest(this);
            profileRequest.requestProfile(language);

            getDataNotification();
        }
    }

    private void getDataNotification(){
        try{
            if (getIntent().getExtras() != null){
                for (String key : getIntent().getExtras().keySet()) {
                    String value = getIntent().getExtras().getString(key);
                    Log.d("Notifications", value);
                }
            }
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    private void bindView() {
        toolbarMenu = findViewById(R.id.toolbar);
        listSlideMenu = findViewById(R.id.list_slide_menu);
        layoutGhost = findViewById(R.id.layout_ghost);
        controlSlide = findViewById(R.id.control_slide);
        imgProfile = findViewById(R.id.img_profile);
        profileName = findViewById(R.id.profile_name);
        profileAddress = findViewById(R.id.profile_address);
        childHeaderDrawer = findViewById(R.id.child_header_drawer);
        headerDrawer = findViewById(R.id.header_drawer);
        linearLayout = findViewById(R.id.liner_content);
        layoutMenu = findViewById(R.id.layout_menu);
        bottomBar = findViewById(R.id.bottom_navigation);
        bottomBar.setOnNavigationItemReselectedListener(this);
        bottomBar.setOnNavigationItemSelectedListener(this);
        BottomNavigationViewHelper.disableShiftMode(bottomBar);

        btnScanQRCode = findViewById(R.id.tab_scan_qrcode);
        btnScanQRCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity(ScanQRCodeActivity.class);
            }
        });
    }

    private void initParameter() {
        SharedPreferences preferences = getSharedPreferences(VariableConnect.keyFile, Context.MODE_PRIVATE);
        currentToken = preferences.getString(getString(R.string.token_login), null);
        Log.d("TOKEN_USER", currentToken);
        url = preferences.getString("web_url", null);
        subscribe = preferences.getString("subscribe_noti", null);

        //Subscribe FireBase
        FirebaseApp.initializeApp(this);
        FirebaseInstanceId.getInstance().getToken();
        FirebaseMessaging.getInstance().subscribeToTopic(subscribe);
        Log.d("Notification", subscribe);

        StackURLController.getInstance().getStackURL().add(url);
        if (currentToken != null && url != null && subscribe != null) {
            ModelCart.getInstance().getKeyModel().setToken(currentToken);
            initBloc(url, true);
        }

        //Subscribe nearby
        DetectBeacon.getInstance(this).setmMessage(new Message(subscribe.getBytes()));
    }

    public void setToolbar() {
        if (Build.VERSION.SDK_INT >= 21) {
            ToolbarManager manager = new ToolbarManager(this);
            manager.setToolbar(toolbarMenu, null, getDrawable(R.drawable.menu_toggle), null);
            setLayoutHeadDrawer();
        }
    }

    private void initBloc(String url, Boolean state) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, AppMenuFragment.newInstance(url,state), VariableConnect.appMenuFragmentKey);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void setLayoutHeadDrawer() {
        ResizeScreen resizeScreen = new ResizeScreen(this);

        RelativeLayout.LayoutParams paramsIMG = (RelativeLayout.LayoutParams) imgProfile.getLayoutParams();
        paramsIMG.height = resizeScreen.widthScreen(3);
        paramsIMG.width = resizeScreen.widthScreen(3);
        imgProfile.setLayoutParams(paramsIMG);

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) childHeaderDrawer.getLayoutParams();
        params.topMargin = getStatusBarHeight() + convertPxtoDp(8);
        childHeaderDrawer.setLayoutParams(params);
    }

    private int convertPxtoDp(int value) {
        Resources resources = getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, resources.getDisplayMetrics());
        return (int) px;
    }

    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }


    @Override
    protected void onResume() {
        super.onResume();
        //Clear Badge Notification
        ShortcutBadger.removeCount(this);
        BadgeController.getInstance(this).setCountBadge(0);

        connectLocation();

        Nearby.getMessagesClient(this).publish(DetectBeacon.getInstance(this).getmMessage());

        //Subscribe
        SubscribeOptions subscribeOptions = new SubscribeOptions.Builder()
                .setStrategy(Strategy.BLE_ONLY).build();
        Nearby.getMessagesClient(this).subscribe(DetectBeacon.getInstance(this), subscribeOptions);
    }

    private void connectLocation(){
        //Start get location
        if (SmartLocation.with(this).location().state().locationServicesEnabled()) {
            LocationParams params = new LocationParams.Builder()
                    .setAccuracy(LocationAccuracy.HIGH)
                    .setInterval(10000)
                    .build();

            SmartLocation.with(this).location()
                    .continuous()
                    .config(params).start(this);
        }
        /*else{
            Intent callGPSSettingIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(callGPSSettingIntent);
        }
        */
    }

    @Override
    protected void onPause() {
        super.onPause();
        DetectBeacon.getInstance(this).setStackBeacon(new ArrayList<BeaconModel>());
        //Stop location
        SmartLocation.with(this).location().stop();
    }

    @Override
    public void onBackPressed() {

    }

    /******* Bottom Bar *************/


    /******* OnLocationUpdate ******************/
    @Override
    public void onLocationUpdated(Location location) {
        double lat = location.getLatitude();
        double Long = location.getLongitude();

        RequestCheckInWork.Data.LocationDevice locationDevice = new RequestCheckInWork.Data.LocationDevice();
        locationDevice.setLatitude(String.valueOf(lat));
        locationDevice.setLongitude(String.valueOf(Long));

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;
        try{
            addresses = geocoder.getFromLocation(lat, Long,1);
            if (addresses != null || addresses.size() > 0){

                for (int i = 0; i <= addresses.get(0).getMaxAddressLineIndex(); i++){
                    geoAddress = addresses.get(0).getSubLocality() + ", " + addresses.get(0).getLocality();
                    profileAddress.setText(geoAddress);
                    Log.d("AddressUser", addresses.get(0).getSubLocality() + ", " + addresses.get(0).getLocality());
                }
            }
        }catch (IOException e){
            // Catch network or other I/O problems.
            e.printStackTrace();
        }catch (IllegalArgumentException e){
            // Catch invalid latitude or longitude values.
            e.printStackTrace();
        }

        ModelCart.getInstance().getRequestCheckInWork().setLocation(locationDevice);
    }

    /******************* onEvent ************************************/

    @Subscribe
    public void onEventProfileResponse(ProfileResponse response){
        if (response.getStatus().equalsIgnoreCase("Success")){
            ModelCart.getInstance().getProfileResult().setData(response.getResult().getData());
            setSlideMenuAdapter(ModelCart.getInstance().getProfileResult().getData());
            Toast.makeText(this, response.getStatus(), Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, response.getResult().getMessage(), Toast.LENGTH_SHORT).show();
        }

        dismissDialog();
    }

    /******************************************************************/


    /**** Slide Menu ***********/
    public void setSlideMenuAdapter(ProfileResponse.ProfileResult.ProfileData dataUser){
        String pathImg = dataUser.getDetail().getImgPath();
        Glide.with(this)
                .load(pathImg)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .into(imgProfile);

        String screenName = dataUser.getDetail().getFirstName() + " " + dataUser.getDetail().getLastName();
        profileName.setText(screenName);

        arrMenu = new ArrayList<>();

        for (int i = 0; i < 2; i++){
            switch (i) {
                case 0:{
                    ArrayList<ProfileResponse.ProfileResult.ProfileData.ProfileLanguage.ListLanguage> listLanguage = new ArrayList<>();
                    for (int j = 0; j < dataUser.getLanguage().getArrLanguage().size(); j++){
                        ProfileResponse.ProfileResult.ProfileData.ProfileLanguage.ListLanguage content = new ProfileResponse.ProfileResult.ProfileData.ProfileLanguage.ListLanguage();
                        content.setValue(dataUser.getLanguage().getArrLanguage().get(j).getValue());
                        content.setValue(dataUser.getLanguage().getArrLanguage().get(j).getCode());
                        listLanguage.add(content);
                    }

                    Item item = new Item(dataUser.getLanguage().getLabel(), listLanguage, null, true);
                    arrMenu.add(item);
                    break;
                }
                case 1:{
                    ArrayList<String> txtChild = new ArrayList<>();
                    int lng = dataUser.getMenus().size();
                    for (int k = 0; k < lng; k++){
                        txtChild.add(dataUser.getMenus().get(k).getLabel());
                    }

                    Item item = new Item("", null, txtChild, false);
                    arrMenu.add(item);
                    break;
                }
                default:{
                    break;
                }
            }
        }

        MenuCategoryAdapter adapter = new MenuCategoryAdapter(arrMenu);
        listSlideMenu.setLayoutManager(new LinearLayoutManager(this));
        listSlideMenu.setHasFixedSize(true);

        listSlideMenu.setAdapter(adapter);
        listSlideMenu.setItemAnimator(new DefaultItemAnimator());
    }

    /***** Touch Slide Menu ************/
    private void setOnTouchView(){
        controlSlide.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                view.setEnabled(true);
                drag(event, linearLayout);
                return false;
            }
        });

        layoutGhost.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                drag(motionEvent, linearLayout);
                return false;
            }
        });
    }

    private void setMotionEventUp(int margin, double speedTouch){
        int marginIn = 20;
        margin -= marginIn;
        if (margin >= widthScreenMenu){
            setToggle(widthScreenMenu - marginIn, speedTouch);
        }else{
            setToggle(margin, speedTouch);
        }

        layoutGhost.setEnabled(true);
    }

    private void setMotionEventMove(int margin, View v){
        int marginIn = 20;
        margin -= marginIn;

        if (margin >= (widthScreenMenu - marginIn)){
            v.setTranslationX(widthScreenMenu - marginIn);

            layoutGhost.setEnabled(true);

        }else {
            v.setTranslationX(margin);
            if (FlyOutContainer.menuCurrentState == FlyOutContainer.MenuState.CLOSED){
                layoutGhost.setEnabled(false);
            }
        }

        root.setAlphaMenu(margin);
        layoutGhost.setVisibility(View.VISIBLE);
    }

    private void drag(final MotionEvent event, final View v){
        int index = event.getActionIndex();
        int action = event.getActionMasked();
        int pointerId = event.getPointerId(index);

        int margin = 0;
        int marginEnd = 150;
        widthScreenMenu = layoutMenu.getRootView().getWidth() - marginEnd;
        haftScreen = (widthScreenMenu - marginEnd) / 2;
        switch(action){
            case MotionEvent.ACTION_MOVE: {
                velocityTracker.addMovement(event);
                velocityTracker.computeCurrentVelocity(1000);
                margin = (int) event.getRawX();
                setMotionEventMove(margin, v);
                break;
            }
            case MotionEvent.ACTION_UP: {
                margin = (int) event.getRawX();
                Log.d("velocity", "X velocity: " + VelocityTrackerCompat.getXVelocity(velocityTracker, pointerId));
                double speedTouch = VelocityTrackerCompat.getXVelocity(velocityTracker, pointerId);
                setMotionEventUp(margin, speedTouch);
                break;
            }
            case MotionEvent.ACTION_DOWN:{
                if (velocityTracker == null){
                    velocityTracker = velocityTracker.obtain();
                }else{
                    velocityTracker.clear();
                }
                velocityTracker.addMovement(event);

                Log.d("MoveDown", "down");
                break;
            }
            case MotionEvent.ACTION_CANCEL:{
                velocityTracker.recycle();
                break;
            }
        }
    }

    public void setToggle(int widthScreen, double speedTouch){
        root.toggleMenu(widthScreen, speedTouch);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            setToggle(0,0);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Subscribe firebase
        FirebaseMessaging.getInstance().unsubscribeFromTopic(subscribe);
        EventBusCart.getInstance().getEventBus().unregister(this);
        Nearby.getMessagesClient(this).unpublish(DetectBeacon.getInstance(this).getmMessage());
        Nearby.getMessagesClient(this).unsubscribe(DetectBeacon.getInstance(this));
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK: {
//                    webView = AppMenuFragment.webView;
//                    if (webView != null){
//                        webView.goBack();
//                    }

                    if (StackURLController.getInstance().getStackURL().size() > 1) {
                        int countStack = StackURLController.getInstance().getStackURL().size() - 2;
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.container, AppMenuFragment.newInstance(StackURLController.getInstance().getStackURL().get(countStack), false),
                                VariableConnect.appMenuFragmentKey);
                        transaction.addToBackStack(null);
                        transaction.commit();

                        StackURLController.getInstance().getStackURL().remove(countStack + 1);
                        Log.d("STACK_URL", StackURLController.getInstance().getStackURL().toString());

                    }
                    break;
                }
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (PermissionKeyNumber.getInstance().getPermissionCameraKey() == requestCode){
            if (grantResults[0] != -1){
                FragmentManager fm = getSupportFragmentManager();
                AppMenuFragment fragment = (AppMenuFragment) fm.findFragmentByTag(VariableConnect.appMenuFragmentKey);
                fragment.setWebView();
            }
        }else if (requestCode == 114) {
            if (grantResults[0] != -1){

            }
        }
    }

    /*** Selector Bottom Navigation **/

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.item_home:{
                initBloc(url, true);
                if (StackURLController.getInstance().getStackURL().size() > 1){
                    StackURLController.getInstance().getStackURL().remove(1);
                }
                StackURLController.getInstance().getStackURL().set(0, url);
                break;
            }
            case R.id.item_noti: {
                String urlNoti = "http://connect06.pakgon.com/core/Homes/noti";
                initBloc(urlNoti, true);
                StackURLController.getInstance().resetStackUrl(url, urlNoti);
                break;
            }

            case R.id.item_contact: {
                String urlContact = "http://connect06.pakgon.com/core/Homes/contact";
                initBloc(urlContact, true);
                StackURLController.getInstance().resetStackUrl(url, urlContact);
                break;
            }

            case R.id.item_profile: {
                String urlContact = "http://connect05.pakgon.com/core/Profiles";
                initBloc(urlContact, true);
                StackURLController.getInstance().resetStackUrl(url, urlContact);
                break;
            }
        }
        return true;
    }

    @Override
    public void onNavigationItemReselected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.item_home:{
                initBloc(url, true);
                if (StackURLController.getInstance().getStackURL().size() > 1){
                    StackURLController.getInstance().getStackURL().remove(1);
                }
                StackURLController.getInstance().getStackURL().set(0, url);
                break;
            }
            case R.id.item_noti: {
                String urlNoti = "http://connect06.pakgon.com/core/Homes/noti";
                initBloc(urlNoti, true);
                StackURLController.getInstance().resetStackUrl(url, urlNoti);
                break;
            }

            case R.id.item_contact: {
                String urlContact = "http://connect06.pakgon.com/core/Homes/contact";
                initBloc(urlContact, true);
                StackURLController.getInstance().resetStackUrl(url, urlContact);
                break;
            }

            case R.id.item_profile: {
                String urlContact = "http://connect05.pakgon.com/core/Profiles";
                initBloc(urlContact, true);
                StackURLController.getInstance().resetStackUrl(url, urlContact);
                break;
            }
        }
    }
}
