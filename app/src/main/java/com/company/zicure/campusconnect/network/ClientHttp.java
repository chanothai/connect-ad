package com.company.zicure.campusconnect.network;

import android.content.Context;

import com.company.zicure.campusconnect.interfaces.LogApi;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Locale;

import com.company.zicure.campusconnect.models.beacon.BeaconRequest;
import com.company.zicure.campusconnect.models.beacon.BeaconResponse;
import com.company.zicure.campusconnect.models.bloc.RequestCheckInWork;
import com.company.zicure.campusconnect.models.bloc.ResponseCheckinWork;
import com.company.zicure.campusconnect.utility.EventBusCart;
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

    public static String URL_API = "http://connect05.pakgon.com/";
    public static String URL_SERVER = "http://connect-uat.pakgon.com";
    public static String URL_SIGNIN = "http://connect-uat.pakgon.com/users/signin";
    public static String URL_OAUTH = "http://oauth-uat.connect.pakgon.com";
    private String language = null;
    private final String LOGAPI = "API_RESPONSE";
    private String jsonStr;

    private Retrofit retrofit = null;
    private LogApi service = null;
    private Gson gson = null;

    public ClientHttp(Context context){
        this.context = context;
        language = Locale.getDefault().getDisplayLanguage().toString();
        gson = new GsonBuilder().disableHtmlEscaping().create();
    }

    public static ClientHttp getInstance(Context context){
        if (me == null){
            me = new ClientHttp(context);
        }

        return me;
    }

    private LogApi setRetrofit(Retrofit retrofit) {
        service = retrofit.create(LogApi.class);
        return service;
    }

    public LogApi getService(String language){
        retrofit = RetrofitAPI.newInstance(URL_API).getRetrofit(language);
        return setRetrofit(retrofit);
    }

    public LogApi getServiceOATH(String language) {
        retrofit = RetrofitAPI.newInstance(URL_SERVER).getRetrofit(language);
        return setRetrofit(retrofit);
    }

    /******* Request Check in for work ***************************/
    public void requestCheckInWork(RequestCheckInWork request){
        Call<ResponseCheckinWork> callCheckin = getService(language).callCheckinWork(request);
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
        Call<BeaconResponse> callBeaconUrl = getService(language).callBeaconUrl(request);
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
