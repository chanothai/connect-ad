package com.company.zicure.campusconnect.activity;

import android.content.Context;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.VelocityTrackerCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.localizationactivity.core.OnLocaleChangedListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.company.zicure.campusconnect.R;
import com.company.zicure.campusconnect.adapter.MenuCategoryAdapter;
import com.company.zicure.campusconnect.customView.BottomNavigationViewHelper;
import com.company.zicure.campusconnect.fragment.AppMenuFragment;

import com.company.zicure.campusconnect.modelview.Item;
import com.company.zicure.campusconnect.nearby.BeaconModel;
import com.company.zicure.campusconnect.nearby.DetectBeacon;
import com.company.zicure.campusconnect.nearby.SubscribeObtionModel;
import com.company.zicure.campusconnect.network.ClientHttp;
import com.company.zicure.campusconnect.network.request.ProfileRequest;
import com.company.zicure.campusconnect.service.BadgeController;
import com.company.zicure.campusconnect.utility.ContextCart;
import com.company.zicure.campusconnect.utility.PermissionKeyNumber;
import com.company.zicure.campusconnect.utility.SharedPreference;
import com.company.zicure.campusconnect.utility.StackURLController;
import com.company.zicure.campusconnect.view.viewgroup.FlyOutContainer;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.MessageListener;
import com.google.android.gms.nearby.messages.Strategy;
import com.google.android.gms.nearby.messages.SubscribeCallback;
import com.google.android.gms.nearby.messages.SubscribeOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.joooonho.SelectableRoundedImageView;
import com.squareup.otto.Subscribe;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.company.zicure.campusconnect.common.BaseActivity;
import com.company.zicure.campusconnect.models.bloc.RequestCheckInWork;
import com.company.zicure.campusconnect.models.profile.ProfileResponse;
import com.company.zicure.campusconnect.utility.EventBusCart;
import com.company.zicure.campusconnect.utility.ModelCart;
import com.company.zicure.campusconnect.utility.ResizeScreen;
import com.company.zicure.campusconnect.utility.ToolbarManager;
import com.company.zicure.campusconnect.utility.VariableConnect;
import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;
import io.nlopez.smartlocation.location.config.LocationAccuracy;
import io.nlopez.smartlocation.location.config.LocationParams;

public class MainMenuActivity extends BaseActivity implements OnLocationUpdatedListener, BottomNavigationView.OnNavigationItemSelectedListener,
        BottomNavigationView.OnNavigationItemReselectedListener, OnLocaleChangedListener {

    /** Make: View **/
    RelativeLayout linearLayout;
    LinearLayout layoutMenu;

    //Bottom navigation
    private BottomNavigationView bottomBar = null;

    //toolbar
    private Toolbar toolbarMenu;

    //list view slide menu
    private ExpandableListView listSlideMenu;
    private FrameLayout layoutGhost,controlSlide;
    private SelectableRoundedImageView imgProfile, btnScanQRCode;
    private TextView profileName,profileAddress;

    //list menu
    private ArrayList<Item> arrMenu = null;
    private RelativeLayout childHeaderDrawer;
    private Context context = this;

    /** Make: properties **/
    //view layout
    private FlyOutContainer root;
    int haftScreen = 0,widthScreenMenu;
    private VelocityTracker velocityTracker = null; // get speed for touch
    private String geoAddress, subscribe, url,currentToken;

    public SubscribeOptions subscribeOptions = null;
    private MessageListener messageListener = null;

    // View Badge
    private View viewBadge;
    private BottomNavigationItemView itemView;

    public void onCreate(Bundle savedInstanceState) {
        initLanguageDevice();
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= 21) {
            EventBusCart.getInstance().getEventBus().register(this);
        }

        root = (FlyOutContainer) getLayoutInflater().inflate(R.layout.activity_main_menu, null);

        ContextCart.getInstance().setContext(this);
        if (savedInstanceState == null) {
            setContentView(root);
            bindView();
            setToolbar();
            setOnTouchView();
            initParameter();
        }else{
            setContentView(root);
            bindView();
            setToolbar();
            setOnTouchView();
            loadData();
        }
    }

    private void initLanguageDevice() {
        String language = Locale.getDefault().getLanguage().toUpperCase();
        if (language.equalsIgnoreCase("TH")){
            setDefaultLanguage(Locale.getDefault().getLanguage());
        }else{
            setDefaultLanguage("EN");
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
        linearLayout = findViewById(R.id.liner_content);
        layoutMenu = findViewById(R.id.layout_menu);
        bottomBar = findViewById(R.id.bottom_navigation);
        bottomBar.setOnNavigationItemReselectedListener(this);
        bottomBar.setOnNavigationItemSelectedListener(this);
        BottomNavigationViewHelper.disableShiftMode(bottomBar);
        createBadgeTab();

        btnScanQRCode = findViewById(R.id.tab_scan_qrcode);
        btnScanQRCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity(ScanQRCodeActivity.class);
            }
        });
    }

    private void loadData(){
        currentToken = SharedPreference.getInstance(this).getRestoreToken();
        Log.d("TOKEN_USER", currentToken);
        url = SharedPreference.getInstance(this).getURL();
        subscribe = SharedPreference.getInstance(this).getSubscribe();
    }

    private void initParameter() {
        loadData();
        initFireBase();
        StackURLController.getInstance().getStackURL().add(url);

        if (currentToken != null && url != null && subscribe != null) {
            ModelCart.getInstance().getKeyModel().setToken(currentToken);
            initBloc(url, true);
        }

        subscribe();
    }

    private void createBadgeTab(){
        BottomNavigationMenuView bottomNavigationMenuView =
                (BottomNavigationMenuView) bottomBar.getChildAt(0);
        View v = bottomNavigationMenuView.getChildAt(1);
        itemView = (BottomNavigationItemView) v;
        viewBadge = LayoutInflater.from(context)
                .inflate(R.layout.custom_notification_badge, bottomNavigationMenuView, false);
    }

    private void checkDataNotify(){
        if (getIntent().getExtras() != null) {
            String badge = getIntent().getExtras().getString("badge", null);
            if (badge != null) {
                BadgeController.getInstance(this).setCountBadge(Integer.parseInt(badge));
                if (itemView.getChildAt(2) == null){
                    itemView.addView(viewBadge);
                }
            }
        }else{
            showBadge();
        }
    }

    public void showBadge(){
        int badge = BadgeController.getInstance(this).getCountBadge();
        if (badge > 0) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (itemView.getChildAt(2) == null){
                        itemView.addView(viewBadge);
                    }
                }
            });
        }
    }

    public void hideBadge() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (itemView.getChildAt(2) != null) {
                    itemView.removeViewAt(2);
                }
            }
        });
    }

    public void initRequestData(String language){
        //init language
        ModelCart.getInstance().getKeyModel().setLanguage(language);
        ProfileRequest profileRequest = new ProfileRequest(this);
        profileRequest.requestProfile(language);
    }

    private void initFireBase(){
        //Subscribe FireBase
        FirebaseApp.initializeApp(this);
        FirebaseInstanceId.getInstance().getToken();
        FirebaseMessaging.getInstance().subscribeToTopic(subscribe);
        Log.d("Notification", subscribe);
    }

    private void initBloc(String url, Boolean state) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, AppMenuFragment.newInstance(url,state), VariableConnect.appMenuFragmentKey);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void setToolbar() {
        if (Build.VERSION.SDK_INT >= 21) {
            ToolbarManager manager = new ToolbarManager(this);
            manager.setToolbar(toolbarMenu, null, getDrawable(R.drawable.menu_toggle), null);
            setLayoutHeadDrawer();
        }
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

    private void subscribe(){
        //Subscribe
        SubscribeOptions subscribeOptions = new SubscribeOptions.Builder()
                .setStrategy(Strategy.BLE_ONLY).build();

        if (messageListener == null) {
            messageListener = DetectBeacon.getInstance(this);
        }

        final SubscribeOptions options = new SubscribeOptions.Builder()
                .setCallback(new SubscribeCallback() {
                    @Override
                    public void onExpired() {
                        super.onExpired();
                        Log.i("NearbyScan", "No longer subscribing");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                DetectBeacon.getInstance(context).setStackBeacon(new ArrayList<BeaconModel>());
                                Nearby.getMessagesClient(context).subscribe(messageListener, SubscribeObtionModel.getInstance().getOptions());
                            }
                        });
                    }
                }).build();


        SubscribeObtionModel.getInstance().setOptions(options);
        Nearby.getMessagesClient(this).subscribe(messageListener,options);
    }

    @Override
    public void onResume() {
        super.onResume();
        initRequestData(Locale.getDefault().getLanguage());
        checkDataNotify();
        connectLocation();
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

    /******* OnLocaleChange *************/
    @Override
    public void onBeforeLocaleChanged() {
        super.onBeforeLocaleChanged();
    }

    @Override
    public void onAfterLocaleChanged() {
        super.onAfterLocaleChanged();
        ProfileRequest profileRequest = new ProfileRequest(this);

        ProfileRequest.Request request = new ProfileRequest.Request();
        request.setAction("change language");
        profileRequest.requestProfile(ModelCart.getInstance().getKeyModel().getLanguage());

        String url = StackURLController.getInstance().getStackURL().get(StackURLController.getInstance().getStackURL().size() - 1);
        initBloc(url, true);
    }

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
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch(IOException e){
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
        Glide.with(context.getApplicationContext())
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
                    Item item = new Item(ModelCart.getInstance().getProfileResult().getData().getLanguage().getLabel(),
                            ModelCart.getInstance().getProfileResult().getData().getLanguage().getArrLanguage(),
                            ModelCart.getInstance().getProfileResult().getData().getMenus(), true);
                    arrMenu.add(item);
                    break;
                }
                case 1:{
                    Item item = new Item("",
                            ModelCart.getInstance().getProfileResult().getData().getLanguage().getArrLanguage(),
                            ModelCart.getInstance().getProfileResult().getData().getMenus(), false);
                    arrMenu.add(item);
                    break;
                }
                default:{
                    break;
                }
            }
        }

        manageExpandMenu();
    }

    private void manageExpandMenu(){
        MenuCategoryAdapter adapter = new MenuCategoryAdapter(this, arrMenu);
        listSlideMenu.setAdapter(adapter);
        listSlideMenu.expandGroup(1, true);
        listSlideMenu.expandGroup(0, true);
        listSlideMenu.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                if (groupPosition == 1){
                    return true;
                }else{
                    return false;
                }
            }
        });
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
        if (Build.VERSION.SDK_INT >= 24) {
            EventBusCart.getInstance().getEventBus().unregister(this);
        }

//        Nearby.getMessagesClient(this).unpublish(DetectBeacon.getInstance(this).getmMessage());
        Nearby.getMessagesClient(this).unsubscribe(DetectBeacon.getInstance(this));

        //Subscribe firebase
        FirebaseMessaging.getInstance().unsubscribeFromTopic(subscribe);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK: {

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
                String urlNoti = "http://commu-uat.connect.pakgon.com/Notis/notiindex";
                initBloc(urlNoti, true);
                StackURLController.getInstance().resetStackUrl(url, urlNoti);

                //badge
                if (itemView != null) {
                    this.hideBadge();
                    BadgeController.getInstance(context).removeBadge();
                }
                break;
            }

            case R.id.item_contact: {
                String urlContact = ClientHttp.URL_SERVER + "/core/Homes/contact";
                initBloc(urlContact, true);
                StackURLController.getInstance().resetStackUrl(url, urlContact);
                break;
            }

            case R.id.item_profile: {
                String urlContact = ClientHttp.URL_SERVER + "/core/Profiles";
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
                String urlNoti = "http://commu-uat.connect.pakgon.com/Notis/notiindex";
                initBloc(urlNoti, true);
                StackURLController.getInstance().resetStackUrl(url, urlNoti);

                //badge
                if (itemView != null) {
                    this.hideBadge();
                    BadgeController.getInstance(context).removeBadge();
                }

                break;
            }

            case R.id.item_contact: {
                String urlContact = ClientHttp.URL_SERVER + "/core/Homes/contact";
                initBloc(urlContact, true);
                StackURLController.getInstance().resetStackUrl(url, urlContact);
                break;
            }

            case R.id.item_profile: {
                String urlContact = ClientHttp.URL_SERVER + "/core/Profiles";
                initBloc(urlContact, true);
                StackURLController.getInstance().resetStackUrl(url, urlContact);
                break;
            }
        }
    }
}
