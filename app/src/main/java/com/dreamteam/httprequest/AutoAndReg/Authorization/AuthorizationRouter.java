package com.dreamteam.httprequest.AutoAndReg.Authorization;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.dreamteam.httprequest.AutoAndReg.Authorization.Entity.AuthDataObject;
import com.dreamteam.httprequest.Interfaces.PresenterInterface;
import com.dreamteam.httprequest.MainActivity;
import com.dreamteam.httprequest.Service.EventService;

public class AuthorizationRouter {

    private MainActivity activity;

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

    //переключение на окно создания Юзера
    public void createUserToAuth(AuthDataObject authDataObject, PresenterInterface delegate){
        Toast.makeText(activity, "Введите личные данные", Toast.LENGTH_LONG).show();
        activity.openEditProfile(null, null,delegate, "User");
    }

    public AuthDataObject getAuthDataObject(){
        return activity.authDataObject;
    }

    public void setAuthData(AuthDataObject authDataObject){
        activity.authDataObject = authDataObject;
    }

    public void getUserID(String userID){
        activity.userID = userID;
        activity.saveSharedPreferences(userID);
        activity.bottomNavigationView.setVisibility(View.VISIBLE);
        activity.showBottomNavigationView(activity.bottomNavigationView);

        // Создаем PendingIntent для Task1
        Intent intent = new Intent(activity, EventService.class);
        PendingIntent pi = activity.createPendingResult(1, intent, 0);
        // Создаем Intent для вызова сервиса, кладем туда параметр времени
        // и созданный PendingIntent
        intent.putExtra("userID", userID)
                .putExtra("Pending", pi);
        // стартуем сервис
        activity.startService(intent);

        activity.openProfile();
    }


    //метод для скрытия клавиатуры
    private void hideKeyboard(){
        InputMethodManager inputManager =
                (InputMethodManager) activity.
                        getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

}
