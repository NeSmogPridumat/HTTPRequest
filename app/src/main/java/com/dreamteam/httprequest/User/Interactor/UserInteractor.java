package com.dreamteam.httprequest.User.Interactor;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.util.Log;

import com.dreamteam.httprequest.Data.ConstantConfig;
import com.dreamteam.httprequest.Group.Entity.GroupData.Group;
import com.dreamteam.httprequest.HTTPConfig;
import com.dreamteam.httprequest.HTTPManager;
import com.dreamteam.httprequest.Interfaces.UserFromHTTPManagerInterface;
import com.dreamteam.httprequest.User.Protocols.PresenterUserInterface;
import com.dreamteam.httprequest.User.Entity.UserData.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayOutputStream;
import java.net.SocketTimeoutException;
import java.util.ArrayList;


public class UserInteractor implements UserFromHTTPManagerInterface {

    public final static String TAG = "UserInteractor";

    ConstantConfig constantConfig = new ConstantConfig();

    HTTPConfig config = new HTTPConfig();

    HTTPManager httpManager = HTTPManager.get();

    HTTPConfig httpConfig = new HTTPConfig();

    private PresenterUserInterface delegate;

    public UserInteractor(PresenterUserInterface delegate) {
        this.delegate = delegate;
    }

//----------------------------------ОТПРАВКА В HTTPMANAGER---------------------------------------//

    public void getUser(String id) {//----------------------------------отправка запроса на получение User по id
        final String path = config.serverURL + config.userPORT + config.reqUser + httpConfig.ID_PARAM + id;
        new Thread(new Runnable() {//---------------------------------------------------------------запуск в фоновом потоке
            @Override
            public void run() {
                httpManager.getRequest(path, constantConfig.USER_TYPE, UserInteractor.this);//----------отправка в HTTPManager
            }
        }).start();
    }


    public void postUser(String name, String surname) {//--------------отправка post-запроса на сервер
        final User user = createUser(name, surname);
        Gson gson = new Gson();
        final String jsonObject = gson.toJson(user);
        final String path = config.serverURL + config.userPORT + config.reqUser;
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


    public void getAfterPostUser(byte[] byteArray) {
        final User user = createUserOfBytes(byteArray);
        String path = config.serverURL + config.userPORT + config.reqUser + httpConfig.ID_PARAM + user.id;
        httpManager.getRequest(path, constantConfig.USER_TYPE, UserInteractor.this);
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
    public void getUserEditResponse(){
        delegate.openUser ();
    }


    @Override
    public void error(Throwable t) {//--------------------------------------------------------------Обработка ошибки
        String error = null;
        if (t instanceof SocketTimeoutException) {
            error = "Ошибка ожидания сервера";
        }
        if (t instanceof NullPointerException) {
            error = "Объект не найден";
        }
        delegate.error(error);
        Log.e(TAG, "Failed server" + t.toString());
    }

    @Override
    public void errorHanding(int resposeCode) {

    }


    public void getUserResponse(byte[] byteArray) {//----------------------------------------------получение json ответа, преобразование его в User и вывод в основной поток
        Log.i("UserInteractor", "jsonString");
        try {
            final User user = createUserOfBytes(byteArray);
            if (user.equals(null)) {
                String error = "Объект не существует";
                delegate.error(error);
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


    public void getImageResponse(User user) {//------------------------------------------------------получение картинки
        try {
            String imageUrl = config.serverURL + config.userPORT + user.content.mediaData.image;
            //Log.i(TAG, "Поток " + Thread.currentThread().getName());
            httpManager.getRequest(imageUrl, constantConfig.IMAGE_TYPE, UserInteractor.this);
        } catch (Exception ioe) {
            Log.e(TAG, "Error downloading image", ioe);
        }
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

    public void getImage(byte[] byteArray) {//-------------------------------------------------------получение картинки(преобразование в bitmap)
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

    public void getGroups (String userId){
        final String path = httpConfig.serverURL + httpConfig.groupPORT + httpConfig.reqGroup +
                httpConfig.reqUser + httpConfig.USER_ID_PARAM + userId;

        new Thread(new Runnable() {
            @Override
            public void run() {
                httpManager.getRequest(path, constantConfig.GET_GROUP_TYPE, UserInteractor.this);
            }
        }).start();
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
        final User user = gson.fromJson(jsonString, User.class);
        return user;
    }

    private ArrayList<Group> createGroupsOfBytes (byte[] byteArray){//----------------------создание массива групп из массива байтов
        Gson gson = new Gson();
        String jsonString = new String(byteArray);
        return gson.fromJson(jsonString, new TypeToken<ArrayList<Group>>(){}.getType());
    }

    //запрос на изменение объекта
    public void putUser(final User user, final Bitmap bitmap){//------------------------------------Отправка User

        final String urlPath = config.serverURL + config.userPORT + config.reqUser;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Gson gson = new Gson();
                    if(bitmap != null){
                        user.content.mediaData.image = decodeBitmapInBase64(bitmap);
                    }
                    final String jsonObject = gson.toJson(user);
                    httpManager.putRequest(urlPath, jsonObject, constantConfig.PUT_USER, UserInteractor.this);
                } catch (Exception error) {
                    error(error);
                }
            }
        }).start();
    }

    //декодирование Bitmap в Base64
    private String decodeBitmapInBase64 (Bitmap bitmap){//------------------------------------------декодирование Bitmap в Base64
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        // Получаем изображение из потока в виде байтов
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return constantConfig.PREFIX + Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    public void getGroupForList (String userID){
        final String path = httpConfig.serverURL + httpConfig.groupPORT + httpConfig.reqGroup +
            httpConfig.reqUser + httpConfig.USER_ID_PARAM + userID;
        new Thread(new Runnable() {
            @Override
            public void run() {
                httpManager.getRequest(path, constantConfig.GET_GROUP_FOR_LIST_TYPE, UserInteractor.this);
            }
        }).start();
    }
}


