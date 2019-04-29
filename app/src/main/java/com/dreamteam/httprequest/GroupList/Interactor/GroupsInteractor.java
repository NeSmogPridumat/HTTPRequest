package com.dreamteam.httprequest.GroupList.Interactor;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.util.Log;

import com.dreamteam.httprequest.Data.ConstantConfig;
import com.dreamteam.httprequest.Data.RequestInfo;
import com.dreamteam.httprequest.Group.Entity.GroupData.Group;
import com.dreamteam.httprequest.Group.Entity.GroupData.GroupMediaData;
import com.dreamteam.httprequest.GroupList.Protocols.GroupsPresenterInterface;
import com.dreamteam.httprequest.HTTPConfig;
import com.dreamteam.httprequest.HTTPManager;
import com.dreamteam.httprequest.Interfaces.GroupsHTTPManagerInterface;

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
        final String path = httpConfig.serverURL + httpConfig.groupPORT + httpConfig.reqGroup +
                httpConfig.reqUser + httpConfig.USER_ID_PARAM + userId;

        new Thread(new Runnable() {
            @Override
            public void run() {
                httpManager.getRequest(path, constantConfig.GET_GROUP_TYPE, GroupsInteractor.this);
            }
        }).start();
    }

//    public void deleteGroups (final ArrayList<Group> groups){
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                for (int i = 0; i < groups.size(); i++){
//                    groups.get(i).content = null;
//                    String path = httpConfig.serverURL + httpConfig.groupPORT + httpConfig.reqGroup + "/del";
//                    Gson gson = new Gson();
//                    String jsonObject = gson.toJson(groups.get(i));
//
////                    String id = "{id:\"" + groups.get(i).id + "\"}";
//                    try {
//                        httpManager.postRequest(path, jsonObject, DELETE_GROUP, GroupsInteractor.this);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//
//                }
//            }
//        }).start();
//    }

    private void uploadImage(final String groupID, final String pathImage ){
            new Thread(new Runnable() {
            @Override
            public void run() {
                httpManager.getRequest(pathImage,constantConfig.IMAGE_TYPE + ":" + groupID, GroupsInteractor.this);
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
            byte[] copyArray = byteArray;
            prepareGetBitmapOfByte(parsingStringType(type)[1], copyArray);
        } else if(type.equals(constantConfig.DELETE_GROUP)){
//            delegate.
            Log.i(TAG, "Сообщение");
            delegate.answerDeleteGroups();
//            delegate.answerDeleteGroups();
        } else if (type.equals(constantConfig.POST_GROUP)){
            delegate.answerAddGroup();
        }
    }

    @Override
    public void error(Throwable t) {

    }

    @Override
    public void errorHanding(int resposeCode) {

    }

    //-----------------------Обработка данных из HTTP MANAGER-------------------------------------//

    private synchronized void prepareGetBitmapOfByte(final String groupID, byte[] byteArray){
        if (byteArray != null){
            Handler mainHandler = new Handler(Looper.getMainLooper());
            Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

            bitmap = Bitmap.createScaledBitmap(bitmap, 150, 150, false);
            final Bitmap finalBitmap = bitmap;

            byteArray = null;
            bitmap = null;
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
        //TODO: узнать что делает final
         final ArrayList<Group> groupCollection = createGroupsOfBytes(byteArray);
        if (groupCollection == null){
            String error = " ";
            delegate.error(error);
        } else {

            Handler mainHandler = new Handler(Looper.getMainLooper());
            Runnable myRunnable = new Runnable() {
                @Override
                public void run() {
                    delegate.answerGetGroups(groupCollection);
                }
            };
            mainHandler.post(myRunnable);

            ArrayList<Group> grs = groupCollection;
            getImageRequest(grs);
        }
    }

    //-----------------------------------------------------------------------------------------//

    private void getImageRequest (ArrayList<Group> groupCollection){//-------------------------------отправка запросов на получение картинок для списка групп
        if (groupCollection != null){
            for (int i = 0 ; i<groupCollection.size(); i++){
                Group group = groupCollection.get(i);
                String pathImage = httpConfig.serverURL + httpConfig.groupPORT + group.content.mediaData.image;
                uploadImage(group.id, pathImage);
            }
        }
    }

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

    private void deleteGroups (final ArrayList<SelectData> arrayList, String type){
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < arrayList.size(); i++){

                    setNullSelectData(arrayList.get(i));

                    //собираем путь запроса
                    String path = httpConfig.serverURL + httpConfig.groupPORT + httpConfig.reqGroup + httpConfig.DEL;//TODO
                    Gson gson = new Gson();
                    String jsonObject = gson.toJson(arrayList.get(i));

                    try {
                        httpManager.postRequest(path, jsonObject, constantConfig.DELETE_GROUP, GroupsInteractor.this);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
        }).start();
    }

    public void addGroup(final Group group, final Bitmap bitmap, final RequestInfo requestInfo){
        final String path = httpConfig.serverURL + httpConfig.groupPORT + httpConfig.reqGroup;

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final String jsonObject = createJsonObject(group, bitmap, requestInfo);
                    httpManager.postRequest(path, jsonObject, constantConfig.POST_GROUP, GroupsInteractor.this);
                } catch (Exception error) {
                    error(error);
                }
            }
        }).start();
    }

    private String createJsonObject(Group group,Bitmap bitmap, RequestInfo requestInfo){
        Gson gson = new Gson();
        if(bitmap != null){
            group.content.mediaData = new GroupMediaData();
            group.content.mediaData.image = decodeBitmapInBase64(bitmap);
        }
        String jsonGroup = gson.toJson(group);
        String jsonRequestInfo = gson.toJson(requestInfo);
        StringBuffer sg = new StringBuffer(jsonGroup);
        sg.deleteCharAt(sg.length()-1);
        StringBuffer sr = new StringBuffer(jsonRequestInfo);
        sr.deleteCharAt(0);
        String jsonObject = (sg + "," + sr);
        return jsonObject;
    }

    private String decodeBitmapInBase64 (Bitmap bitmap){//------------------------------------------декодирование Bitmap в Base64
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);//TODO: поставил 50, потому что долго грузит большие картинки
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


