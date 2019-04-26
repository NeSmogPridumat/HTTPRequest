package com.dreamteam.httprequest.AutoAndReg.Authorization;

import com.dreamteam.httprequest.MainActivity;

public class AuthorizationRouter {

    MainActivity activity;

    public AuthorizationRouter (MainActivity activity){
        this.activity = activity;
    }

    public void getRegistration(){
        activity.openRegistration();
    }
}
