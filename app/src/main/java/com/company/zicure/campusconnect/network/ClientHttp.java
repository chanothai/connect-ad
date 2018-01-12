package com.company.zicure.campusconnect.network;

import android.content.Context;
import android.util.Log;

import com.company.zicure.campusconnect.interfaces.LogApi;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;

import gallery.zicure.company.com.modellibrary.models.BaseResponse;
import gallery.zicure.company.com.modellibrary.models.DataModel;
import gallery.zicure.company.com.modellibrary.models.beacon.BeaconRequest;
import gallery.zicure.company.com.modellibrary.models.beacon.BeaconResponse;
import gallery.zicure.company.com.modellibrary.models.bloc.RequestCheckInWork;
import gallery.zicure.company.com.modellibrary.models.bloc.ResponseBlocUser;
import gallery.zicure.company.com.modellibrary.models.bloc.ResponseCheckinWork;
import gallery.zicure.company.com.modellibrary.models.contact.RequestAddContact;
import gallery.zicure.company.com.modellibrary.models.contact.RequestDeleteProfile;
import gallery.zicure.company.com.modellibrary.models.contact.ResponseAddContact;
import gallery.zicure.company.com.modellibrary.models.contact.ResponseContactList;
import gallery.zicure.company.com.modellibrary.models.contact.ResponseDeleteProfile;
import gallery.zicure.company.com.modellibrary.models.login.LoginRequest;
import gallery.zicure.company.com.modellibrary.models.login.LoginResponse;
import gallery.zicure.company.com.modellibrary.models.profile.ResponseIDCard;
import gallery.zicure.company.com.modellibrary.models.quiz.ResponseQuiz;
import gallery.zicure.company.com.modellibrary.models.register.RegisterRequest;
import gallery.zicure.company.com.modellibrary.models.register.ResponseRegister;
import gallery.zicure.company.com.modellibrary.models.register.ResponseUniversities;
import gallery.zicure.company.com.modellibrary.models.register.VerifyRequest;
import gallery.zicure.company.com.modellibrary.models.register.VerifyResponse;
import gallery.zicure.company.com.modellibrary.models.updatepassword.RequestForgotPassword;
import gallery.zicure.company.com.modellibrary.models.updatepassword.RequestUpdatePassword;
import gallery.zicure.company.com.modellibrary.models.updatepassword.ResponseForgotPassword;
import gallery.zicure.company.com.modellibrary.models.updatepassword.ResponseUpdatePassword;
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
        String urlIdentityServer = "http://connect02.pakgon.com/";
        retrofit = RetrofitAPI.newInstance(urlIdentityServer).getRetrofit();
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

    public void requestORG(){
        Call<ResponseUniversities> callORG = service.callORG();
        callORG.enqueue(new Callback<ResponseUniversities>() {
            @Override
            public void onResponse(Call<ResponseUniversities> call, Response<ResponseUniversities> response) {
                try{
                    EventBusCart.getInstance().getEventBus().post(response.body());
                }catch (NullPointerException e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseUniversities> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }


    public void registerSecure(DataModel user){
        Call<BaseResponse> callRegis = service.callRegisterSecure(user);
        callRegis.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                Log.d("RegisterResponse",gson.toJson(response.body()));
                if (response.body() != null){
                    EventBusCart.getInstance().getEventBus().post(response.body());
                }else{
                    BaseResponse baseResponse = new BaseResponse();
                    BaseResponse.Result result = new BaseResponse.Result();
                    result.setError("Data null");
                    result.setSuccess("");
                    baseResponse.setResult(result);

                    EventBusCart.getInstance().getEventBus().post(baseResponse);
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                t.printStackTrace();
                BaseResponse baseResponse = new BaseResponse();
                BaseResponse.Result result = new BaseResponse.Result();
                result.setError("Connect Error");
                result.setSuccess("");
                baseResponse.setResult(result);

                EventBusCart.getInstance().getEventBus().post(baseResponse);
            }
        });
    }

    public void register(RegisterRequest request) {
        Call<ResponseRegister> callRegister = service.callRegister(request);
        callRegister.enqueue(new Callback<ResponseRegister>() {
            @Override
            public void onResponse(Call<ResponseRegister> call, Response<ResponseRegister> response) {
                try{
                    EventBusCart.getInstance().getEventBus().post(response.body());
                }catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseRegister> call, Throwable t) {

            }
        });
    }

    public void loginSecure(DataModel model){
        Log.d("LoginModel", gson.toJson(model));
        Call<BaseResponse> loginResponseCall = service.responseLogin(model);
        loginResponseCall.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                Log.d("LoginResponse", new Gson().toJson(response.body()));
                try {
                    EventBusCart.getInstance().getEventBus().post(response.body());
                }catch (NullPointerException e){
                    ResponseError.setResponse("Result: null");
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                t.printStackTrace();
                ResponseError.setResponse("Connect or Model Error");
            }
        });
    }

    public void login(LoginRequest request) {
        Call<LoginResponse> callLogin = service.callLogin(request);
        callLogin.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                try {
                    EventBusCart.getInstance().getEventBus().post(response.body());
                }catch (NullPointerException e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {

            }
        });
    }

    public void forgotPassword(RequestForgotPassword request) {
        Call<ResponseForgotPassword> callUpdate = service.requestForgotPassword(request);
        callUpdate.enqueue(new Callback<ResponseForgotPassword>() {
            @Override
            public void onResponse(Call<ResponseForgotPassword> call, Response<ResponseForgotPassword> response) {
                try{
                    EventBusCart.getInstance().getEventBus().post(response.body());
                }catch (NullPointerException e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseForgotPassword> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public void updatePassword(RequestUpdatePassword request){
        Call<ResponseUpdatePassword> callUpdate = service.requestUpdatePassword(request);
        callUpdate.enqueue(new Callback<ResponseUpdatePassword>() {
            @Override
            public void onResponse(Call<ResponseUpdatePassword> call, Response<ResponseUpdatePassword> response) {
                try{
                    EventBusCart.getInstance().getEventBus().post(response.body());
                }catch (NullPointerException e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseUpdatePassword> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public void checkVersionApp(){
        Call<BaseResponse> validateVersion = service.checkVersion();
        validateVersion.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                try{
                    if (response.body() != null){
                        EventBusCart.getInstance().getEventBus().post(response.body());
                    }
                }catch (NullPointerException e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public void verifyUser(VerifyRequest request){
        Call<VerifyResponse> verify = service.verifyUser(request);
        verify.enqueue(new Callback<VerifyResponse>() {
            @Override
            public void onResponse(Call<VerifyResponse> call, Response<VerifyResponse> response) {
                try{
                    EventBusCart.getInstance().getEventBus().post(response.body());
                }catch (NullPointerException e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<VerifyResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public void requestAuthToken(DataModel dataModel){
        Log.d("DataModel", gson.toJson(dataModel));
        Call<BaseResponse> authToken = service.requestAuthenToken(dataModel);
        authToken.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                try{
                    EventBusCart.getInstance().getEventBus().post(response.body());
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });
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

    public void requestProfile(String token, String userId) {
        HashMap<String, String> map = new HashMap<>();
        if (userId == null){
            map.put("authToken", token);
        }else{
            map.put("user_id", userId);
        }

        Call<ResponseIDCard> profile = service.requestProfile(map);
        profile.enqueue(new Callback<ResponseIDCard>() {
            @Override
            public void onResponse(Call<ResponseIDCard> call, Response<ResponseIDCard> response) {
                try{
                    EventBusCart.getInstance().getEventBus().post(response.body());
                }catch (NullPointerException e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseIDCard> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public void requestContactList(String token) {
        HashMap<String, String> map = new HashMap<>();
        map.put("authToken", token);
        Call<ResponseContactList> contactList = service.requestContact(map);
        contactList.enqueue(new Callback<ResponseContactList>() {
            @Override
            public void onResponse(Call<ResponseContactList> call, Response<ResponseContactList> response) {
                try{
                    EventBusCart.getInstance().getEventBus().post(response.body());
                }catch(NullPointerException e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseContactList> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public void requestAddContact(RequestAddContact contact) {
        Call<ResponseAddContact> addContact = service.requestAddContact(contact);
        addContact.enqueue(new Callback<ResponseAddContact>() {
            @Override
            public void onResponse(Call<ResponseAddContact> call, Response<ResponseAddContact> response) {
                try{
                    EventBusCart.getInstance().getEventBus().post(response.body());
                }catch (NullPointerException e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseAddContact> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public void requestDeleteContact(RequestDeleteProfile request){
        Call<ResponseDeleteProfile> deleteContact = service.callDeleteProfile(request);
        deleteContact.enqueue(new Callback<ResponseDeleteProfile>() {
            @Override
            public void onResponse(Call<ResponseDeleteProfile> call, Response<ResponseDeleteProfile> response) {
                try{
                    EventBusCart.getInstance().getEventBus().post(response.body());
                }catch (NullPointerException e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseDeleteProfile> call, Throwable t) {
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

    /*********************/

    public static class ResponseError {
        public static void setResponse(String message){
            BaseResponse baseResponse = new BaseResponse();
            BaseResponse.Result result = new BaseResponse.Result();
            result.setSuccess("");
            result.setError(message);
            baseResponse.setResult(result);

            EventBusCart.getInstance().getEventBus().post(baseResponse);
        }
    }
}
