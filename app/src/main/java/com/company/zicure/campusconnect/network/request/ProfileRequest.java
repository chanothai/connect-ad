package com.company.zicure.campusconnect.network.request;

import android.content.Context;

import com.company.zicure.campusconnect.network.ClientHttp;

import gallery.zicure.company.com.modellibrary.models.profile.ProfileResponse;
import gallery.zicure.company.com.modellibrary.utilize.EventBusCart;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by macintosh on 17/1/18.
 */

public class ProfileRequest {
    private Context context = null;
    public ProfileRequest(Context context) {
        this.context = context;
    }

    public void requestProfile(String language){
        Call<ProfileResponse> callProfile = ClientHttp.getInstance(context).getService(language).callProfile();
        callProfile.enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {
                try{
                    EventBusCart.getInstance().getEventBus().post(response.body());
                }catch (NullPointerException e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ProfileResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
}
