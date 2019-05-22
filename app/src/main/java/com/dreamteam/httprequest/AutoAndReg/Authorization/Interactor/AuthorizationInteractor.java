package com.dreamteam.httprequest.AutoAndReg.Authorization.Interactor;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.util.Log;

import com.dreamteam.httprequest.AddOrEditInfoProfile.InfoProfileData;
import com.dreamteam.httprequest.AutoAndReg.Authorization.Entity.AnswerAuth;
import com.dreamteam.httprequest.AutoAndReg.Authorization.Entity.AuthData;
import com.dreamteam.httprequest.AutoAndReg.Authorization.Entity.AuthDataObject;
import com.dreamteam.httprequest.AutoAndReg.Authorization.Entity.Content;
import com.dreamteam.httprequest.AutoAndReg.Authorization.Entity.Token;
import com.dreamteam.httprequest.AutoAndReg.Authorization.Protocols.AuthorizationHTTPManagerInterface;
import com.dreamteam.httprequest.AutoAndReg.Authorization.Protocols.AuthorizationPresenterInterface;
import com.dreamteam.httprequest.Data.ConstantConfig;
import com.dreamteam.httprequest.Data.RequestInfo;
import com.dreamteam.httprequest.HTTPConfig;
import com.dreamteam.httprequest.HTTPManager;
import com.dreamteam.httprequest.Interfaces.OutputHTTPManagerInterface;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.SocketTimeoutException;

import static android.support.constraint.Constraints.TAG;

public class AuthorizationInteractor implements AuthorizationHTTPManagerInterface {

    private AuthDataObject authDataObject = new AuthDataObject();
    private HTTPManager httpManager = HTTPManager.get();
    private HTTPConfig httpConfig = new HTTPConfig();
    private ConstantConfig constantConfig = new ConstantConfig();

    private AuthorizationPresenterInterface delegate;

    private final String CREATE_LOGIN = "create login";
    private final String ENABLE_USER_AUTH = "enable user auth";
    private final String GET_USER_TOKEN = "get user token";

    private final String ADD_USER_AUTH = "add user auth";

    public AuthorizationInteractor(AuthorizationPresenterInterface delegate){
        this.delegate = delegate;
    }

    //создание логина
    public void createLogin (String login, String password){
        authDataObject.authData = new AuthData();
        authDataObject.authData.login = login;
        authDataObject.authData.pass = password;
        final String path = httpConfig.serverURL + httpConfig.SERVER_AUTH + httpConfig.AUTH + httpConfig.CREATE;

        startPostRequest(path, authDataObject, CREATE_LOGIN, AuthorizationInteractor.this);
    }

    @Override
    public void response(byte[] byteArray, String type) {
        if (byteArray != null){
            if (type.equals(CREATE_LOGIN)) {
                answerCreateLogin(byteArray);
            } else if (type.equals(ENABLE_USER_AUTH)){
                answerEnableUserAuth(byteArray);
            }else if (type.equals(ADD_USER_AUTH)){
                Log.i("TAF", "TTTTTTTTTTTTT");
                answerCreateUserToAuth(byteArray);
            } else if ((type.equals(GET_USER_TOKEN))){
                answerGetUserToken(byteArray);
            }

            }else {
            Log.i("Error", "NOT TRUE");
        }

    }

    @Override
    public void error(Throwable t) {
        String title = null;
        String description  = null;
        if (t instanceof SocketTimeoutException) {
            title = "Ошибка соединения с сервером";
            description = "Проверте соединение с интернетом. Не удается подключится с серверу";
        }
        if (t instanceof NullPointerException) {
            title = "Объект не найден";
            description = "";
        }
        Handler mainHandler = new Handler(Looper.getMainLooper());
        final String finalTitle = title;
        final String finalDescription = description;
        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                delegate.error(finalTitle, finalDescription);
            }
        };
        mainHandler.post(myRunnable);
        Log.e(TAG, "Failed server" + t.toString());
    }

    @Override
    public void errorHanding(final int responseCode) {
        Handler mainHandler = new Handler(Looper.getMainLooper());
        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                delegate.errorHanding(responseCode);
            }
        };
        mainHandler.post(myRunnable);
    }

    private void answerGetUserToken(byte[] byteArray){
        Gson gson = new Gson();
        String jsonString = new String(byteArray);
        final Token token = gson.fromJson(jsonString, Token.class);
        Handler mainHandler = new Handler(Looper.getMainLooper());
        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                delegate.answerGetUserToken(token);
                httpManager.token = token;
            }
        };
        mainHandler.post(myRunnable);
    }

    //ответ от создания логина, получаем boolean
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

    //получаем ответ после проверки ключа в формате boolean
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
        AnswerAuth answerAuth =  gson.fromJson(jsonString, AnswerAuth.class);
        boolean responseBoolean = false;
        if (answerAuth.status.equals("ok")){
            responseBoolean = true;
        }
        final boolean answer = responseBoolean;
        return answer;
    }

    public void enableUserAuth (String key, final AuthDataObject authDataObject){
        authDataObject.authData.key = key;
        final String path = httpConfig.serverURL + httpConfig.SERVER_AUTH + httpConfig.AUTH + httpConfig.ENABLE;

        startPostRequest(path, authDataObject, ENABLE_USER_AUTH, AuthorizationInteractor.this);
    }

    public void createUserToAuth (InfoProfileData infoProfileData, final AuthDataObject authDataObject){
        Log.i("ЧЁ", "НАДО??????");
        authDataObject.content = new Content();
        authDataObject.content.simpleData.name = infoProfileData.title;
        authDataObject.content.simpleData.surname = infoProfileData.description;
        if (infoProfileData.imageData != null) {
            authDataObject.content.mediaData.image = decodeBitmapInBase64(infoProfileData.imageData);
        }
        authDataObject.authData.key = null;

        final String path = httpConfig.serverURL + httpConfig.SERVER_AUTH + httpConfig.AUTH + httpConfig.USER;

        startPostRequest(path, authDataObject, ADD_USER_AUTH, AuthorizationInteractor.this);
    }

    private void answerCreateUserToAuth(byte[] byteArray){
        final boolean answer = createBooleanOfBytes(byteArray);
        Handler mainHandler = new Handler(Looper.getMainLooper());
        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                delegate.answerCreateUserToAuth(answer);
            }
        };
        mainHandler.post(myRunnable);
    }

    public void getUserToken (AuthDataObject authDataObject){
        authDataObject.authData.key = null;
        final String path = httpConfig.serverURL + httpConfig.SERVER_AUTH + httpConfig.AUTH;

        startPostRequest(path, authDataObject, GET_USER_TOKEN, AuthorizationInteractor.this);
    }

    //================================SUPPORT METHODS============================================//

    private String decodeBitmapInBase64 (Bitmap bitmap){//------------------------------------------декодирование Bitmap в Base64
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);//TODO: поставил 50, потому что долго грузит большие картинки
        // Получаем изображение из потока в виде байтов
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return constantConfig.PREFIX + Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    private void startPostRequest (final String path, final AuthDataObject authDataObject,
                                   final String type, final OutputHTTPManagerInterface delegate){
        new Thread(new Runnable() {//---------------------------------------------------------------запуск в фоновом потоке
            @Override
            public void run() {
                try {
                    Gson gson = new Gson();
                    final String jsonObject = gson.toJson(authDataObject);
                    httpManager.postRequest(path, jsonObject, type, delegate);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
