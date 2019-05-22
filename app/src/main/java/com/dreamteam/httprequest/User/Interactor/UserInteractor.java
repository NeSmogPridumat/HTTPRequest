package com.dreamteam.httprequest.User.Interactor;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.util.Log;

import com.dreamteam.httprequest.Data.AddData;
import com.dreamteam.httprequest.Data.ConstantConfig;
import com.dreamteam.httprequest.Data.RequestInfo;
import com.dreamteam.httprequest.Group.Entity.GroupData.Group;
import com.dreamteam.httprequest.Group.Entity.GroupData.GroupMediaData;
import com.dreamteam.httprequest.HTTPConfig;
import com.dreamteam.httprequest.HTTPManager;
import com.dreamteam.httprequest.Interfaces.OutputHTTPManagerInterface;
import com.dreamteam.httprequest.Interfaces.UserFromHTTPManagerInterface;
import com.dreamteam.httprequest.User.Protocols.PresenterUserInterface;
import com.dreamteam.httprequest.User.Entity.UserData.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayOutputStream;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;


public class UserInteractor implements UserFromHTTPManagerInterface {

    private final static String TAG = "UserInteractor";

    private ConstantConfig constantConfig = new ConstantConfig();

    private HTTPConfig config = new HTTPConfig();

    private HTTPManager httpManager = HTTPManager.get();

    private HTTPConfig httpConfig = new HTTPConfig();

    private PresenterUserInterface delegate;

    public UserInteractor(PresenterUserInterface delegate) {
        this.delegate = delegate;
    }

//----------------------------------ОТПРАВКА В HTTPMANAGER---------------------------------------//

    public void getUser(String id) {//----------------------------------отправка запроса на получение User по id
        final String path = config.serverURL + config.SERVER_GETTER + config.reqUser + httpConfig.ID_PARAM + id;

        startGetRequest(path, constantConfig.USER_TYPE, UserInteractor.this);
    }

    public void postUser(String name, String surname) {//--------------отправка post-запроса на сервер
        final User user = createUser(name, surname);
        Gson gson = new Gson();
        final String jsonObject = gson.toJson(user);
        final String path = config.serverURL + config.SERVER_SETTER + config.reqUser;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    httpManager.postRequest(path, jsonObject, constantConfig.POST_USER, UserInteractor.this);
                } catch (Exception error) {
                    error(error);
                }
            }
        }).start();
    }

    private void getAfterPostUser(byte[] byteArray) {
        final User user = createUserOfBytes(byteArray);
        String path = config.serverURL + config.SERVER_GETTER + config.reqUser + httpConfig.ID_PARAM + user.id;
        startGetRequest(path, constantConfig.USER_TYPE, UserInteractor.this);
    }

//----------------------------------------ПОЛУЧЕНИЕ И ОБРАБОТКА ДАННЫХ-----------------------------//

    @Override
    public void response(byte[] byteArray, String type) {//----------------------------------------получение ответа от HTTPManager и распределение по типу
        if (type.equals(constantConfig.USER_TYPE)) {
            getUserResponse(byteArray);
        } else if (type.equals(constantConfig.POST_USER)) {
            //TODO:возможно вынести в отдельный метод (либо совместить с getUser?)
            getAfterPostUser(byteArray);
        } else if (type.equals(constantConfig.IMAGE_TYPE)) {
            getImage(byteArray);
        } else if (type.equals(constantConfig.GET_GROUP_TYPE)) {
            prepareGetGroupsResponse(byteArray);
        }else if (type.equals(constantConfig.PUT_USER)){
            getUserEditResponse();
        } else if (type.equals(constantConfig.GET_GROUP_FOR_LIST_TYPE)){
            prepareGetGroupsForListResponse(byteArray);
        }
    }

    //открытие окна профиля после получения ответа
    private void getUserEditResponse(){
        delegate.openUserAfterEdit ();
    }

    @Override
    public void error(Throwable t) {//--------------------------------------------------------------Обработка ошибки
        String title = null;
        String description  = null;
        if (t instanceof SocketTimeoutException|| t instanceof ConnectException) {
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
    public void errorHanding(int resposeCode) {

    }


    private void getUserResponse(byte[] byteArray) {//----------------------------------------------получение json ответа, преобразование его в User и вывод в основной поток
        Log.i("UserInteractor", "jsonString");
        try {
            final User user = createUserOfBytes(byteArray);
            if (user.equals(null)) {
                String error = "Объект не существует";
                delegate.error(error, "");
            }

            Handler mainHandler = new Handler(Looper.getMainLooper());
            Runnable myRunnable = new Runnable() {
                @Override
                public void run() {
                    delegate.answerGetUser(user);
                }
            };
            mainHandler.post(myRunnable);

            getImageResponse(user);
            getGroups(user.id);
            Thread.currentThread().interrupted();
        } catch (Exception error) {
            error(error);
        }
    }

    private void getImageResponse(User user) {//------------------------------------------------------получение картинки
        String imageUrl = config.serverURL + config.SERVER_GETTER + user.content.mediaData.image;
        startGetRequest(imageUrl, constantConfig.IMAGE_TYPE, UserInteractor.this);
    }

    private void prepareGetGroupsResponse(byte[] byteArray){
        //TODO: узнать что делает final
        final ArrayList<Group> groupCollection = createGroupsOfBytes(byteArray);
        int size = 0;
        if (groupCollection != null){
            size = groupCollection.size();
        }

        Handler mainHandler = new Handler(Looper.getMainLooper());
        final int finalSize = size;
        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                delegate.answerGetGroups(finalSize);
            }
        };
        mainHandler.post(myRunnable);
    }

    private void prepareGetGroupsForListResponse (byte[] byteArray){
        final ArrayList<Group> groupCollection = createGroupsOfBytes(byteArray);

        if (groupCollection != null){
            Handler mainHandler = new Handler(Looper.getMainLooper());
            Runnable myRunnable = new Runnable() {
                @Override
                public void run() {
                    delegate.answerGetGroupsForList(groupCollection);
                }
            };
            mainHandler.post(myRunnable);
        }
    }

    private void getImage(byte[] byteArray) {//-------------------------------------------------------получение картинки(преобразование в bitmap)
        if(byteArray != null) {
            final Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

            Handler mainHandler = new Handler(Looper.getMainLooper());
            Runnable myRunnable = new Runnable() {
                @Override
                public void run() {
                    delegate.answerGetImage(bitmap);
                }
            };
            mainHandler.post(myRunnable);
            Thread.currentThread().interrupted();
        }
    }

    private void getGroups(String userId){
        final String path = httpConfig.serverURL + httpConfig.SERVER_GETTER + httpConfig.reqGroup +
                httpConfig.reqUser + httpConfig.USER_ID_PARAM + userId;

        startGetRequest(path, constantConfig.GET_GROUP_TYPE, UserInteractor.this);
    }

    public void getGroupForList (String userID){
        final String path = httpConfig.serverURL + httpConfig.SERVER_GETTER + httpConfig.reqGroup +
                httpConfig.reqUser + httpConfig.USER_ID_PARAM + userID;

        startGetRequest(path, constantConfig.GET_GROUP_FOR_LIST_TYPE, UserInteractor.this);
    }

    private User createUser(String name, String surname) {
        User user = new User();
        user.content.simpleData.name = name;
        user.content.simpleData.surname = surname;
        return user;
    }

    private User createUserOfBytes(byte[] byteArray){
        Gson gson = new Gson();
        String jsonString = new String(byteArray);
        return gson.fromJson(jsonString, User.class);
    }


    //запрос на изменение объекта
    public void putUser(final User user, final Bitmap bitmap){
        final RequestInfo requestInfo = getRequestInfo(user);
        final String urlPath = config.serverURL + config.SERVER_SETTER + config.reqUser;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Gson gson = new Gson();
                    if(bitmap != null){
                        requestInfo.addData.content.mediaData.image = decodeBitmapInBase64(bitmap);
                    }
                    final String jsonObject = gson.toJson(requestInfo);
                    httpManager.putRequest(urlPath, jsonObject, constantConfig.PUT_USER, UserInteractor.this);
                } catch (Exception error) {
                    error(error);
                }
            }
        }).start();
    }

    //===============================SUPPORT METHODS===============================================//

    private RequestInfo getRequestInfo (User user){
        RequestInfo requestInfo = new RequestInfo();
        requestInfo.creatorID = user.id;
        requestInfo.addData = new AddData();
        requestInfo.addData.id = user.id;
        requestInfo.addData.content.mediaData = new GroupMediaData();
        requestInfo.addData.content.simpleData.name = user.content.simpleData.name;
        requestInfo.addData.content.simpleData.surname = user.content.simpleData.surname;
        return requestInfo;
    }

    //декодирование Bitmap в Base64
    private String decodeBitmapInBase64 (Bitmap bitmap){//------------------------------------------декодирование Bitmap в Base64
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        // Получаем изображение из потока в виде байтов
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return constantConfig.PREFIX + Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    private ArrayList<Group> createGroupsOfBytes (byte[] byteArray){//----------------------создание массива групп из массива байтов
        ArrayList<Group> groups = null;
        if (byteArray != null) {
            Gson gson = new Gson();
            String jsonString = new String(byteArray);
            return gson.fromJson(jsonString, new TypeToken<ArrayList<Group>>() {
            }.getType());
        }
        return groups;
    }

    //get-запросы на сервер
    private void startGetRequest(final String path, final String type, final OutputHTTPManagerInterface delegate){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    httpManager.getRequest(path, type, delegate);
                } catch (Exception error) {
                    error(error);
                }
            }
        }).start();
    }
}


