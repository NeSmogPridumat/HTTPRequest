package com.dreamteam.httprequest.SelectList.SelectInteractor;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;

import com.dreamteam.httprequest.Data.ConstantConfig;
import com.dreamteam.httprequest.Data.HTTPConfig;
import com.dreamteam.httprequest.GroupList.Interactor.GroupsInteractor;
import com.dreamteam.httprequest.HTTPManager.HTTPManager;
import com.dreamteam.httprequest.Interfaces.OutputHTTPManagerInterface;
import com.dreamteam.httprequest.SelectList.Protocol.SelctedHTTPOutputFromHTTPManager;
import com.dreamteam.httprequest.SelectList.Protocol.SelectPresenter;
import com.dreamteam.httprequest.User.Entity.UserData.Users;
import com.google.gson.Gson;

public class SelectInteractor implements SelctedHTTPOutputFromHTTPManager {
    private HTTPManager httpManager = HTTPManager.get();
    private HTTPConfig httpConfig = new HTTPConfig();
    private ConstantConfig constantConfig = new ConstantConfig();
    private SelectPresenter delegate;

    public SelectInteractor (SelectPresenter delegate){
        this.delegate = delegate;
    }

    public void getUsers(String name){
        String path = httpConfig.serverURL + httpConfig.SERVER_GETTER + httpConfig.USER + "?name=" + name + "&expanded=true";
        startGetRequest(path, constantConfig.GET_USERS_FOR_ADD_STEP_1_TYPE, SelectInteractor.this);
    }

    public void getImageRequest (String groupId){//-------------------------------отправка запросов на получение картинок для списка групп
        String pathImage = httpConfig.serverURL + httpConfig.SERVER_GETTER + httpConfig.GROUP + "/" + groupId + httpConfig.IMAGE;
        uploadImage(groupId, pathImage);

    }

    private void uploadImage(final String groupID, final String pathImage ){
        startGetRequest(pathImage,constantConfig.IMAGE_TYPE + ":" + groupID,
                SelectInteractor.this);
    }

    @Override
    public void response(byte[] byteArray, String type) {
        if (type.equals(constantConfig.GET_USERS_FOR_ADD_STEP_1_TYPE)){
            prepareGetUsers(byteArray);
        }else if ((parsingStringType(type).length > 1) && (parsingStringType(type)[0]
                .equals(constantConfig.IMAGE_TYPE))) {
            prepareGetBitmapOfByte(parsingStringType(type)[1], byteArray);
        }
    }

    @Override
    public void error(Throwable t) {

    }

    @Override
    public void errorHanding(int responseCode, String type) {

    }

    private synchronized void prepareGetBitmapOfByte(final String groupID, byte[] byteArray){
        if (byteArray != null){
            Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            bitmap = Bitmap.createScaledBitmap(bitmap, 150, 150, false);
            final Bitmap finalBitmap = bitmap;

            Handler mainHandler = new Handler(Looper.getMainLooper());
            Runnable myRunnable = new Runnable() {
                @Override
                public void run() {
                    delegate.answerGetImageGroups(groupID, finalBitmap);
                }
            };
            mainHandler.post(myRunnable);
        }
    }

    private void prepareGetUsers(byte[] byteArray){
        final Users users = createUserOfBytes(byteArray);
        for (int i = 0; i< users.users.size(); i++) {
            getImageRequest(users.users.get(i).id);
        }
        Handler mainHandler = new Handler(Looper.getMainLooper());
        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                delegate.answerGetUsers(users.users);
            }
        };
        mainHandler.post(myRunnable);

    }

    private void startGetRequest(final String path, final String type,
                                 final OutputHTTPManagerInterface delegate){
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

    private Users createUserOfBytes(byte[] byteArray){
        Gson gson = new Gson();
        String jsonString = new String(byteArray);
        return gson.fromJson(jsonString, Users.class);
    }

    private String[] parsingStringType(String string){//--------------------------------------------разбор строки (getImageGroupType + ":" + groupID)
        String delimiter = ":";
        return string.split(delimiter);
    }
}
