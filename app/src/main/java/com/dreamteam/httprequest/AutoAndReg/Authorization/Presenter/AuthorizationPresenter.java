package com.dreamteam.httprequest.AutoAndReg.Authorization.Presenter;

import com.dreamteam.httprequest.AutoAndReg.Authorization.AuthorizationRouter;
import com.dreamteam.httprequest.MainActivity;

public class AuthorizationPresenter {

    private AuthorizationRouter router;

    public AuthorizationPresenter(MainActivity activity){
        router = new AuthorizationRouter(activity);
    }

    public void getRegistration(){
        router.getRegistration();
    }
}
