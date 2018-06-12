package com.company.zicure.campusconnect.utility;

import com.company.zicure.campusconnect.models.KeyModel;
import com.company.zicure.campusconnect.models.bloc.RequestCheckInWork;
import com.company.zicure.campusconnect.models.bloc.ResponseBlocUser;
import com.company.zicure.campusconnect.models.profile.ProfileResponse;

/**
 * Created by 4GRYZ52 on 10/25/2016.
 */

public class ModelCart {
    private static ModelCart me = null;
    private KeyModel keyModel = null;
    private ResponseBlocUser userInfo = null;
    private RequestCheckInWork.Data requestCheckInWork = null;
    private ProfileResponse.ProfileResult profileResult = null;

    private ModelCart() {
        keyModel = new KeyModel();
        userInfo = new ResponseBlocUser();
        requestCheckInWork = new RequestCheckInWork.Data();
        profileResult = new ProfileResponse.ProfileResult();
    }

    public static ModelCart getInstance(){
        if (me == null){
            me = new ModelCart();
        }

        return me;
    }

    public ProfileResponse.ProfileResult getProfileResult(){
        return profileResult;
    }

    public KeyModel getKeyModel(){
        return keyModel;
    }

    public ResponseBlocUser getUserBloc(){
        return userInfo;
    }

    public RequestCheckInWork.Data getRequestCheckInWork(){
        return requestCheckInWork;
    }

    public void clearAllData(){
        keyModel = new KeyModel();
        userInfo = new ResponseBlocUser();
        requestCheckInWork = new RequestCheckInWork.Data();
        profileResult = new ProfileResponse.ProfileResult();
    }
}
