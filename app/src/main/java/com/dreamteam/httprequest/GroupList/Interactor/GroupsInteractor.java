package com.dreamteam.httprequest.GroupList.Interactor;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;

import com.dreamteam.httprequest.Data.AddData;
import com.dreamteam.httprequest.Data.ConstantConfig;
import com.dreamteam.httprequest.Data.RequestInfo;
import com.dreamteam.httprequest.Group.Entity.GroupData.Group;
import com.dreamteam.httprequest.Group.Entity.GroupData.GroupMediaData;
import com.dreamteam.httprequest.GroupList.Protocols.GroupsPresenterInterface;
import com.dreamteam.httprequest.Data.HTTPConfig;
import com.dreamteam.httprequest.HTTPManager.HTTPManager;
import com.dreamteam.httprequest.Interfaces.GroupsHTTPManagerInterface;

import com.dreamteam.httprequest.Interfaces.OutputHTTPManagerInterface;
import com.dreamteam.httprequest.SelectedList.SelectData;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class GroupsInteractor implements GroupsHTTPManagerInterface {

    private final String TAG = "GroupsInteractor";

    private HTTPConfig httpConfig = new HTTPConfig();
    private ConstantConfig constantConfig = new ConstantConfig();

    private HTTPManager httpManager = HTTPManager.get();

    private GroupsPresenterInterface delegate;

    public GroupsInteractor(GroupsPresenterInterface delegate){
        this.delegate = delegate;
    }

    //-------------------------------Входные функции из Presenter, отправка в HTTP MANAGER---------//

    public void getGroups (String userId){
        final String path = httpConfig.serverURL + httpConfig.SERVER_GETTER + httpConfig.reqGroup +
                httpConfig.reqUser + httpConfig.USER_ID_PARAM + userId;

        startGetRequest(path, constantConfig.GET_GROUP_TYPE, GroupsInteractor.this);
    }

    private void uploadImage(final String groupID, final String pathImage ){
        startGetRequest(pathImage,constantConfig.IMAGE_TYPE + ":" + groupID,
                GroupsInteractor.this);
    }

    private void getImageRequest (ArrayList<Group> groupCollection){//-------------------------------отправка запросов на получение картинок для списка групп
        if (groupCollection != null){
            for (int i = 0 ; i<groupCollection.size(); i++){
                Group group = groupCollection.get(i);
                String pathImage = httpConfig.serverURL + httpConfig.SERVER_GETTER
                        + group.content.mediaData.image;
                uploadImage(group.id, pathImage);
            }
        }
    }

    private void deleteGroups (final ArrayList<SelectData> arrayList, String type){
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < arrayList.size(); i++){
                    setNullSelectData(arrayList.get(i));

                    //собираем путь запроса
                    String path = httpConfig.serverURL + httpConfig.SERVER_SETTER
                            + httpConfig.reqGroup + httpConfig.DEL;//TODO
                    Gson gson = new Gson();
                    String jsonObject = gson.toJson(arrayList.get(i));
                    try {
                        httpManager.postRequest(path, jsonObject, constantConfig.DELETE_GROUP,
                                GroupsInteractor.this);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public void addGroup(final Group group, final Bitmap bitmap, final RequestInfo requestInfo){
        final String path = httpConfig.serverURL + httpConfig.SERVER_SETTER + httpConfig.reqGroup;

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    requestInfo.addData = new AddData();
                    requestInfo.addData.content = group.content;
                    final String jsonObject = createJsonObject(bitmap, requestInfo);
                    httpManager.postRequest(path, jsonObject, constantConfig.POST_GROUP,
                            GroupsInteractor.this);
                } catch (Exception error) {
                    error(error);
                }
            }
        }).start();
    }


    //--------------------------Получение данных из HTTP MANAGER и вызов функций обработки---------//

    @Override
    public void response(byte[] byteArray, String type) {
        if (type.equals(constantConfig.GET_GROUP_TYPE)){
            prepareGetGroupsResponse(byteArray);
        } else if ((parsingStringType(type).length > 1) && (parsingStringType(type)[0]
                .equals(constantConfig.IMAGE_TYPE))){
            prepareGetBitmapOfByte(parsingStringType(type)[1], byteArray);
        } else if(type.equals(constantConfig.DELETE_GROUP)){
            delegate.answerDeleteGroups();
        } else if (type.equals(constantConfig.POST_GROUP)){
            delegate.answerAddGroup();
        }
    }

    @Override
    public void error(final Throwable t) {
        Handler mainHandler = new Handler(Looper.getMainLooper());
        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                delegate.error(t);
            }
        };
        mainHandler.post(myRunnable);
    }

    @Override
    public void errorHanding(int resposeCode, String type) {

    }

    //-----------------------Обработка данных из HTTP MANAGER-------------------------------------//

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

    private void prepareGetGroupsResponse(byte[] byteArray){
        if (byteArray != null){
                final ArrayList<Group> groupCollection = createGroupsOfBytes(byteArray);
            if (groupCollection == null){
                delegate.error(new NullPointerException());
            } else {
                Handler mainHandler = new Handler(Looper.getMainLooper());
                Runnable myRunnable = new Runnable() {
                    @Override
                    public void run() {
                        delegate.answerGetGroups(groupCollection);
                    }
                };
                mainHandler.post(myRunnable);

                getImageRequest(groupCollection);
            }
        }
    }

    //================================SUPPORT METHODS======================================//

    private String[] parsingStringType(String string){//--------------------------------------------разбор строки (getImageGroupType + ":" + groupID)
        String delimiter = ":";
        return string.split(delimiter);
    }

    private ArrayList<Group> createGroupsOfBytes (byte[] byteArray){//----------------------создание массива групп из массива байтов
        Gson gson = new Gson();
        String jsonString = new String(byteArray);
        return gson.fromJson(jsonString, new TypeToken<ArrayList<Group>>(){}.getType());
    }

    //отправляем полученный выбранный список на удаление
    public void inputSelect(final ArrayList<SelectData> arrayList, String type){
        if (type.equals(constantConfig.DELETE)){
            deleteGroups(arrayList, type);
        }
    }

    //get-запросы на сервер
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


    private String createJsonObject(Bitmap bitmap, RequestInfo requestInfo){
        Gson gson = new Gson();
        if(bitmap != null){
            requestInfo.addData.content.mediaData = new GroupMediaData();
            requestInfo.addData.content.mediaData.image = decodeBitmapInBase64(bitmap);
        }
        return gson.toJson(requestInfo);
    }

    private String decodeBitmapInBase64 (Bitmap bitmap){//------------------------------------------декодирование Bitmap в Base64
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);//поставил 50, потому что долго грузит большие картинки
        // Получаем изображение из потока в виде байтов
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return constantConfig.PREFIX + Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    //зануляем ненужные поля для построения тела запроса
    private void setNullSelectData(SelectData selectData){
        selectData.title = null;
        selectData.image = null;
        selectData.description = null;
        selectData.check = null;
    }
}


