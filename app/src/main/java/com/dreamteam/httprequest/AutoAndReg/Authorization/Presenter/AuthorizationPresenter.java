package com.dreamteam.httprequest.AutoAndReg.Authorization.Presenter;

import android.graphics.Bitmap;

import com.dreamteam.httprequest.AddOrEditInfoProfile.Data.InfoProfileData;
import com.dreamteam.httprequest.AutoAndReg.Authorization.AuthorizationRouter;
import com.dreamteam.httprequest.AutoAndReg.Authorization.Entity.AuthData;
import com.dreamteam.httprequest.AutoAndReg.Authorization.Entity.AuthDataObject;
import com.dreamteam.httprequest.AutoAndReg.Authorization.Entity.Token;
import com.dreamteam.httprequest.AutoAndReg.Authorization.Interactor.AuthorizationInteractor;
import com.dreamteam.httprequest.AutoAndReg.Authorization.Protocols.AuthorizationPresenterInterface;
import com.dreamteam.httprequest.AutoAndReg.Authorization.Protocols.AuthorizationViewInterface;
import com.dreamteam.httprequest.Data.RequestInfo;
import com.dreamteam.httprequest.MainActivity;
import com.dreamteam.httprequest.SelectedList.SelectData;

import java.util.ArrayList;

public class AuthorizationPresenter implements AuthorizationPresenterInterface {

    private AuthorizationRouter router;
    private AuthorizationInteractor authorizationInteractor;
    private AuthDataObject authDataObject;
    private AuthorizationViewInterface delegate;

    public AuthorizationPresenter(MainActivity activity,  AuthorizationViewInterface delegate){
        router = new AuthorizationRouter(activity);
        authorizationInteractor = new AuthorizationInteractor(this);
        this.delegate = delegate;
    }

    //создание логина
    public void createLogin(String login, String password){
        authorizationInteractor.createLogin(login, password);
    }

    public void enterUser(String login, String password){
        authDataObject = new AuthDataObject();
        authDataObject.authData = new AuthData();
        authDataObject.authData.login = login;
        authDataObject.authData.pass = password;
        authDataObject.authData.key = null;
        authorizationInteractor.getUserToken(authDataObject);
    }

    //открытие контроллера для регистрации
    public void getRegistration(){
        router.getRegistration();
    }

    public void enableUserAuth(String key, AuthDataObject authDataObject){

        //отправляем ключ на проверку
        authorizationInteractor.enableUserAuth(key, authDataObject);

        //сохраняем данные с ключом в актиности
        authDataObject.authData.key = key;
        router.setAuthData(authDataObject);
    }

    //ответ создания логина, если true, открываем контроллер для ввода ключа
    @Override
    public void answerCreateLogin(boolean answer, AuthDataObject authDataObject) {
        if (answer) {
            router.getKeyLogin(authDataObject);
        }
    }

    //если ответ true открываем фрагмент для изменения и добавления данных
    @Override
    public void answerEnableUserAuth(boolean answer, AuthDataObject authDataObject) {
        if (answer) {
            router.createUserToAuth(authDataObject, this);
        }
    }

    @Override
    public void showDialog() {

    }

    @Override
    public void answerDialog(int i) {

    }

    @Override
    public void forResult(Bitmap bitmap) {

    }

    @Override
    public void inputSelect(ArrayList<SelectData> arrayList, String type) {

    }

    //получаем измененные данные
    @Override
    public void editInfo(InfoProfileData infoProfileData, RequestInfo requestInfo, String type) {
        authDataObject = router.getAuthDataObject();
        authorizationInteractor.createUserToAuth(infoProfileData, authDataObject);
    }

    @Override
    public void answerCreateUserToAuth(boolean answer){
        if (answer){
            authorizationInteractor.getUserToken(router.getAuthDataObject());
        }
    }

    @Override
    public void answerGetUserToken(Token token) {
        router.getUserID(token.id);
    }

    @Override
    public void errorHanding(int responseCode) {
        if(responseCode == 409){
            router.createUserToAuth(router.getAuthDataObject(), this);
        }
        if (responseCode == 404){
            delegate.showNotFound();//TODO: возможно надо передать во View
        }
    }

    @Override
    public void error(String title, String description) {
        delegate.error(title, description);
    }
}
