package com.dreamteam.httprequest.AutoAndReg.Authorization.Interactor;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.dreamteam.httprequest.AddOrEditInfoProfile.InfoProfileData;
import com.dreamteam.httprequest.AutoAndReg.Authorization.Entity.AuthDataObject;
import com.dreamteam.httprequest.AutoAndReg.Authorization.Protocols.AuthorizationHTTPManagerInterface;
import com.dreamteam.httprequest.AutoAndReg.Authorization.Protocols.AuthorizationPresenterInterface;
import com.dreamteam.httprequest.HTTPConfig;
import com.dreamteam.httprequest.HTTPManager;
import com.dreamteam.httprequest.User.Entity.UserData.User;
import com.google.gson.Gson;

import java.io.IOException;

public class AuthorizationInteractor implements AuthorizationHTTPManagerInterface {

    private AuthDataObject authDataObject = new AuthDataObject();
    private HTTPManager httpManager = HTTPManager.get();
    private HTTPConfig httpConfig = new HTTPConfig();

    private AuthorizationPresenterInterface delegate;

    private final String CREATE_LOGIN = "create login";
    private final String ENABLE_USER_AUTH = "enable user auth";

    public AuthorizationInteractor(AuthorizationPresenterInterface delegate){
        this.delegate = delegate;
    }

    public void createLogin (String login, String password){
        authDataObject.authData.login = login;
        authDataObject.authData.pass = password;
        Gson gson = new Gson();
        final String jsonObject = gson.toJson(authDataObject);
        final String path = httpConfig.serverURL + "9000" + httpConfig.AUTH + httpConfig.CREATE;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    httpManager.postRequest(path, jsonObject, CREATE_LOGIN, AuthorizationInteractor.this);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void response(byte[] byteArray, String type) {
        if (byteArray != null){
            if (type.equals(CREATE_LOGIN)) {
                answerCreateLogin(byteArray);
            } else if (type.equals(ENABLE_USER_AUTH)){
                answerEnableUserAuth(byteArray);
            }

            }else {
            Log.i("Error", "NOT TRUE");
        }

    }

    @Override
    public void error(Throwable t) {

    }

    private void answerCreateLogin (byte[] byteArray){
        final boolean answer = createBooleanOfBytes(byteArray);
        Handler mainHandler = new Handler(Looper.getMainLooper());
        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                delegate.answerCreateLogin(answer, authDataObject);
            }
        };
        mainHandler.post(myRunnable);
    }

    private void answerEnableUserAuth(byte[] byteArray){
        final boolean answer = createBooleanOfBytes(byteArray);
        Handler mainHandler = new Handler(Looper.getMainLooper());
        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                delegate.answerEnableUserAuth(answer, authDataObject);
            }
        };
        mainHandler.post(myRunnable);
    }

    private boolean createBooleanOfBytes(byte[] byteArray){
        Gson gson = new Gson();
        String jsonString = new String(byteArray);
        final boolean answer = gson.fromJson(jsonString, Boolean.class);
        return answer;
    }

    public void enableUserAuth (String key, AuthDataObject authDataObject){
        authDataObject.authData.key = key;
        final String path = httpConfig.serverURL + "9000" + httpConfig.AUTH + httpConfig.ENABLE;
        Gson gson = new Gson();
        final String jsonObject = gson.toJson(authDataObject);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    httpManager.postRequest(path, jsonObject, ENABLE_USER_AUTH, AuthorizationInteractor.this);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void createUserToAuth (InfoProfileData infoProfileData, AuthDataObject authDataObject){
        Log.i("ЧЁ", "НАДО??????");
    }
}
