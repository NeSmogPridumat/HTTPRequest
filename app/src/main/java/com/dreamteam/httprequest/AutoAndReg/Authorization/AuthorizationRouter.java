package com.dreamteam.httprequest.AutoAndReg.Authorization;

import com.dreamteam.httprequest.AutoAndReg.Authorization.Entity.AuthDataObject;
import com.dreamteam.httprequest.Interfaces.PresenterInterface;
import com.dreamteam.httprequest.MainActivity;

public class AuthorizationRouter {

    MainActivity activity;

    public AuthorizationRouter (MainActivity activity){
        this.activity = activity;
    }

    public void getRegistration(){
        activity.openRegistration();
    }

    public void getKeyLogin(AuthDataObject authDataObject){
        activity.openKeyRegistration();
        activity.authDataObject = authDataObject;
    }

    public void createUserToAuth(AuthDataObject authDataObject, PresenterInterface delegate){
        activity.authDataObject = authDataObject;
        activity.openEditProfile(null, delegate, "User");
    }

    public AuthDataObject getAuthDataObject(){
        return activity.authDataObject;
    }
}
