package com.company.zicure.campusconnect.network;

import android.content.Context;
import android.util.Log;

import com.company.zicure.campusconnect.interfaces.LogApi;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;
import java.util.Locale;

import gallery.zicure.company.com.modellibrary.models.beacon.BeaconRequest;
import gallery.zicure.company.com.modellibrary.models.beacon.BeaconResponse;
import gallery.zicure.company.com.modellibrary.models.bloc.RequestCheckInWork;
import gallery.zicure.company.com.modellibrary.models.bloc.ResponseBlocUser;
import gallery.zicure.company.com.modellibrary.models.bloc.ResponseCheckinWork;
import gallery.zicure.company.com.modellibrary.utilize.EventBusCart;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by BallOmO on 10/13/2016 AD.
 */

public class ClientHttp {
    private Context context = null;
    private static ClientHttp me;

    private final String LOGAPI = "API_RESPONSE";
    private String jsonStr;

    private Retrofit retrofit = null;
    private LogApi service = null;
    private Gson gson = null;

    public ClientHttp(Context context){
        this.context = context;
        String urlIdentityServer = "http://connect06.pakgon.com/";
        String language = Locale.getDefault().getDisplayLanguage().toString();
        retrofit = RetrofitAPI.newInstance(urlIdentityServer).getRetrofit(language);
        service = retrofit.create(LogApi.class);
        gson = new GsonBuilder().disableHtmlEscaping().create();
    }

    public static ClientHttp getInstance(Context context){
        if (me == null){
            me = new ClientHttp(context);
        }

        return me;
    }

    public LogApi getService(){
        return service;
    }

    public void requestUserBloc(String authToken) {
        HashMap<String, String> map = new HashMap<>();
        map.put("authToken", authToken);
        Call<ResponseBlocUser> userBloc = service.requestUserBloc(map);
        userBloc.enqueue(new Callback<ResponseBlocUser>() {
            @Override
            public void onResponse(Call<ResponseBlocUser> call, Response<ResponseBlocUser> response) {
                try{
                    EventBusCart.getInstance().getEventBus().post(response.body());
                }catch(NullPointerException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBlocUser> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    /******* Request Check in for work ***************************/
    public void requestCheckInWork(RequestCheckInWork request){
        Call<ResponseCheckinWork> callCheckin = service.callCheckinWork(request);
        callCheckin.enqueue(new Callback<ResponseCheckinWork>() {
            @Override
            public void onResponse(Call<ResponseCheckinWork> call, Response<ResponseCheckinWork> response) {
                try{
                    EventBusCart.getInstance().getEventBus().post(response.body());
                }catch (NullPointerException e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseCheckinWork> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    /** Beacon Request **/
    public void requestBeaconUrl(BeaconRequest request){
        Call<BeaconResponse> callBeaconUrl = service.callBeaconUrl(request);
        callBeaconUrl.enqueue(new Callback<BeaconResponse>() {
            @Override
            public void onResponse(Call<BeaconResponse> call, Response<BeaconResponse> response) {
                try{
                    EventBusCart.getInstance().getEventBus().post(response.body());
                }catch (NullPointerException e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<BeaconResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
}
