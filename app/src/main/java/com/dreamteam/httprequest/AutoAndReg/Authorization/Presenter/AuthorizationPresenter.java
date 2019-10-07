package com.dreamteam.httprequest.AutoAndReg.Authorization.Presenter;

import android.graphics.Bitmap;

import com.dreamteam.httprequest.AddOrEditInfoProfile.Data.InfoProfileData;
import com.dreamteam.httprequest.AutoAndReg.Authorization.AuthorizationRouter;
import com.dreamteam.httprequest.AutoAndReg.Authorization.Entity.AuthDataObject;
import com.dreamteam.httprequest.AutoAndReg.Authorization.Entity.InfoAndTokenData;
import com.dreamteam.httprequest.AutoAndReg.Authorization.Entity.NewAuth;
import com.dreamteam.httprequest.AutoAndReg.Authorization.Interactor.AuthorizationInteractor;
import com.dreamteam.httprequest.AutoAndReg.Authorization.Protocols.AuthorizationPresenterInterface;
import com.dreamteam.httprequest.AutoAndReg.Authorization.Protocols.AuthorizationViewInterface;
import com.dreamteam.httprequest.Data.RequestInfo;
import com.dreamteam.httprequest.MainActivity;
import com.dreamteam.httprequest.SelectedList.Data.SelectData;

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
    public void createLogin(String login, String password, String name, String surname){
        authorizationInteractor.createLogin(login, password, name, surname);
    }

    public void enterUser(String login, String password){
//        authDataObject = new AuthDataObject();
//        authDataObject.authData = new AuthData();
//        authDataObject.authData.login = login;
//        authDataObject.authData.pass = password;
//        authDataObject.authData.key = null;

        NewAuth newAuth = new NewAuth();
        newAuth.phone = login;
        newAuth.password = password;
        authorizationInteractor.getUserToken(newAuth);
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
    public void answerCreateLogin(String answer, AuthDataObject authDataObject) {

        if (answer != null) {
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
    public void answerDialog(int i, String title, String message, String priority) {

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
            authorizationInteractor.getUserToken(/*router.getAuthDataObject()*/ null);
        }
    }

    @Override
    public void answerGetUserToken(InfoAndTokenData infoAndTokenData) {
        router.getUserID(infoAndTokenData);
    }

    @Override
    public void errorHanding(int responseCode) {
        if(responseCode == 409){
            delegate.showNotFound();
        }
        if (responseCode == 401){
            delegate.showNotFound();
        }
    }

    @Override
    public void error(Throwable t) {
        delegate.error(t);
    }
}
