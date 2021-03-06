package com.company.zicure.campusconnect.interfaces;

import java.util.Map;

import com.company.zicure.campusconnect.models.beacon.BeaconRequest;
import com.company.zicure.campusconnect.models.beacon.BeaconResponse;
import com.company.zicure.campusconnect.models.bloc.RequestCheckInWork;
import com.company.zicure.campusconnect.models.bloc.ResponseBlocUser;
import com.company.zicure.campusconnect.models.bloc.ResponseCheckinWork;

import com.company.zicure.campusconnect.models.profile.ProfileResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;

/**
 * Created by BallOmO on 10/11/2016 AD.
 */
public interface LogApi {
    @GET("Api/userAccessControl.json")
    Call<ResponseBlocUser> requestUserBloc(@QueryMap Map<String, String> category);

    @POST("Beacon/getBlocUrl.json")
    Call<BeaconResponse> callBeaconUrl(@Body BeaconRequest request);

    @POST("Api/timeAttendant.json")
    Call<ResponseCheckinWork> callCheckinWork(@Body RequestCheckInWork requestCheckInWork);

    @POST("api/Profiles/info")
    Call<ProfileResponse> callProfile();
}
