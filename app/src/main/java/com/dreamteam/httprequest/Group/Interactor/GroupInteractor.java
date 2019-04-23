package com.dreamteam.httprequest.Group.Interactor;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;

import com.dreamteam.httprequest.Group.Entity.GroupData.Group;
import com.dreamteam.httprequest.Group.Protocols.GroupPresenterInterface;
import com.dreamteam.httprequest.HTTPConfig;
import com.dreamteam.httprequest.HTTPManager;
import com.dreamteam.httprequest.Interfaces.GroupHTTPMangerInterface;
import com.dreamteam.httprequest.User.Entity.UserData.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

public class GroupInteractor implements GroupHTTPMangerInterface {

    private final static String TAG = "UserInteractor";

    //===========================КОНСТАНТЫ ДЛЯ ТИПОВ ЗАПРОСА===================================//
    private final String GET_GROUP_TYPE = "getUser";
    private final String IMAGE_TYPE = "image";
    private final String MEMBERS_TYPE = "members";

    private GroupPresenterInterface delegate;
    private HTTPManager httpManager = HTTPManager.get();
    private HTTPConfig httpConfig = new HTTPConfig();

    public GroupInteractor(GroupPresenterInterface delegate) {
        this.delegate = delegate;
    }

    public void getGroup(String id) {
        final String path = httpConfig.serverURL + httpConfig.groupPORT + httpConfig.reqGroup + "?id=" + id;
        new Thread(new Runnable() {//---------------------------------------------------------------запуск в фоновом потоке
            @Override
            public void run() {
                httpManager.getRequest(path, GET_GROUP_TYPE, GroupInteractor.this);//----------отправка в HTTPManager
            }
        }).start();
    }

    private void prepareGetGroupResponse(byte[] byteArray) {//-----------------------------------------------получение json ответа, преобразование его в User и вывод в основной поток
        try {
            final Group group = createGroupOfBytes(byteArray);
            if (group == null) {
                String error = "Объект не существует";
                delegate.error(error);
            }

            Handler mainHandler = new Handler(Looper.getMainLooper());
            Runnable myRunnable = new Runnable() {
                @Override
                public void run() {
                delegate.answerGetGroup(group.content.simpleData.title, group.content.simpleData.description);
                }
            };
            mainHandler.post(myRunnable);

            //отправка запроса на получение картинки
            getImageRequest(group);

            //отправка запроса на получение количества участников в группе
            getMembersRequest(group);
            Thread.currentThread().interrupted();
        } catch (Exception error) {
            error(error);
        }
    }

    //метод отправки запроса
    private void getMembersRequest(Group group){
        String membersPath = httpConfig.serverURL + "8100" + httpConfig.reqUser + httpConfig.reqGroup + "?groupID=" + group.id; //"5c4a0105-c5b5-450e-b781-113a21f16e5a"

        httpManager.getRequest(membersPath, MEMBERS_TYPE, GroupInteractor.this);
    }

    private void getImageRequest(Group group) {//-----------------------------------------------------получение картинки

        ThreadLocal tl = new ThreadLocal();
        try {
            tl.set(System.nanoTime());
            String imageUrl = httpConfig.serverURL + httpConfig.groupPORT + group.content.mediaData.image;
            httpManager.getRequest(imageUrl, IMAGE_TYPE, GroupInteractor.this);
        }
        finally {
            tl.remove();
        }
    }

    private Group createGroupOfBytes(byte[] byteArray) {//---------------------------------------------создание User из массива байтов
        Gson gson = new Gson();
        String jsonString = new String(byteArray);
        return gson.fromJson(jsonString, Group.class);
    }

    private void prepareGetImageResponse(byte[] byteArray) {//------------------------------------------------------получение картинки(преобразование в bitmap)
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

    //получение списка членов группы
    private void prepareGetMembersResponse (byte[] byteArray){
        Gson gson = new Gson();
        String jsonString = new String(byteArray);
        final ArrayList<User> members = createMembersOfBytes(byteArray);
//        int size = 0;
//        if(members != null){
//            size = members.size();
//        }
        Handler mainHandler = new Handler(Looper.getMainLooper());
        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                delegate.answerGetMembers(members.size());
            }
        };
        mainHandler.post(myRunnable);
    }

    private ArrayList<User> createMembersOfBytes(byte[] byteArray){
        Gson gson = new Gson();
        String jsonString = new String(byteArray);
        return gson.fromJson(jsonString, new TypeToken<ArrayList<User>>(){}.getType());
    }


    //----------------------------------------ПОЛУЧЕНИЕ ДАННЫХ ОТ HTTP MANAGER И ВЫЗОВ ФУНКЦИЙ ДЛЯ ОБРАБОТКИ-----------------------------//
    @Override
    public void response(byte[] byteArray, String type) {
        if (type.equals(GET_GROUP_TYPE)) {
            prepareGetGroupResponse(byteArray);
        }else if (type.equals(IMAGE_TYPE)) {
            prepareGetImageResponse(byteArray);
            byteArray = null;
        } else if (type.equals(MEMBERS_TYPE)){
            prepareGetMembersResponse(byteArray);
        }
    }

    @Override
    public void error(Throwable t) {

    }
}
