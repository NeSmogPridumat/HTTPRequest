package com.dreamteam.httprequest.AutoAndReg.Authorization;

import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.dreamteam.httprequest.AutoAndReg.Authorization.Entity.AuthDataObject;
import com.dreamteam.httprequest.AutoAndReg.Authorization.Entity.Token;
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

        //сохраняем в Активности данные с логином и паролем(Активность постоянно работает, так что данные не уйдут)
        activity.authDataObject = authDataObject;
    }

    public void createUserToAuth(AuthDataObject authDataObject, PresenterInterface delegate){
        Toast.makeText(activity, "Введите личные данные", Toast.LENGTH_LONG).show();
        activity.openEditProfile(null, delegate, "User");

    }

    public AuthDataObject getAuthDataObject(){
        AuthDataObject authDataObject = activity.authDataObject;
        return authDataObject;
    }

    public void setAuthData(AuthDataObject authDataObject){
        activity.authDataObject = authDataObject;
    }

    public void getUserID(String userID){
        activity.userID = userID;
        activity.saveSharedPreferences(userID);
        activity.openProfile();
    }

    private void hideKeyboard(){
        InputMethodManager inputManager =
                (InputMethodManager) activity.
                        getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public void showNotFound(){
        Toast.makeText(activity, "Пользователь не зарегестрирован", Toast.LENGTH_LONG).show();
    }
}
