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
import com.dreamteam.httprequest.Group.Entity.GroupData.Personal;
import com.dreamteam.httprequest.GroupList.Data.GroupsId;
import com.dreamteam.httprequest.GroupList.Protocols.GroupsPresenterInterface;
import com.dreamteam.httprequest.Data.HTTPConfig;
import com.dreamteam.httprequest.HTTPManager.HTTPManager;
import com.dreamteam.httprequest.Interfaces.GroupsHTTPManagerInterface;

import com.dreamteam.httprequest.Interfaces.OutputHTTPManagerInterface;
import com.dreamteam.httprequest.MultipartUtility;
import com.dreamteam.httprequest.SelectedList.Data.SelectData;
import com.dreamteam.httprequest.database.App;
import com.dreamteam.httprequest.database.Data.GroupDB;
import com.dreamteam.httprequest.database.Group.GroupDataBase;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class GroupsInteractor implements GroupsHTTPManagerInterface {

    private final String TAG = "GroupsInteractor";

    private int countGroups = 0;
    private ArrayList<Group> groups;

    private HTTPConfig httpConfig = new HTTPConfig();
    private ConstantConfig constantConfig = new ConstantConfig();

    private HTTPManager httpManager = HTTPManager.get();

    private GroupsPresenterInterface delegate;

    private File imageFile;

    private GroupDataBase groupDataBase;

    public GroupsInteractor(GroupsPresenterInterface delegate){
        this.delegate = delegate;
        groups = new ArrayList<>();
        groupDataBase = App.getInstance().getGroupDataBase();
    }

    //-------------------------------Входные функции из Presenter, отправка в HTTP MANAGER---------//

    public void getGroups (String userId){
        final String path = httpConfig.serverURL + httpConfig.SERVER_GETTER + httpConfig.reqGroup + "?uid=" + userId; // +
//                httpConfig.reqUser + httpConfig.USER_ID_PARAM + userId;

        startGetRequest(path, constantConfig.GET_GROUPS_TYPE, GroupsInteractor.this);
    }

    private void uploadImage(final String groupID, final String pathImage ){
        startGetRequest(pathImage,constantConfig.IMAGE_TYPE + ":" + groupID,
                GroupsInteractor.this);
    }

    public void getImageRequest (String groupId){//-------------------------------отправка запросов на получение картинок для списка групп

        String pathImage = httpConfig.serverURL + httpConfig.SERVER_GETTER + httpConfig.GROUP + "/" + groupId + httpConfig.IMAGE;
//                        + group.content.mediaData.image;
        uploadImage(groupId, pathImage);

//        ThreadLocal tl = new ThreadLocal();
//        try {
//            tl.set(System.nanoTime());
//            String imageUrl = httpConfig.serverURL + httpConfig.SERVER_GETTER + httpConfig.GROUP + "/" + groupId + httpConfig.IMAGE;
////                    + group.content.mediaData.image;
//            httpManager.getRequest(imageUrl, constantConfig.IMAGE_TYPE, GroupsInteractor.this);
//        }
//        finally {
//            tl.remove();
//        }
    }

    private void deleteGroups (final ArrayList<SelectData> arrayList, String type){
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (SelectData selectData : arrayList){
                    setNullSelectData(selectData);

                    //собираем путь запроса
                    String path = httpConfig.serverURL + httpConfig.SERVER_SETTER
                            + httpConfig.reqGroup + httpConfig.DEL;//TODO
                    Gson gson = new Gson();
                    String jsonObject = gson.toJson(selectData);
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

    public void addGroup(final Group group, final File imageFile, final RequestInfo requestInfo){
        final String path = httpConfig.serverURL + httpConfig.SERVER_SETTER + httpConfig.reqGroup;
        this.imageFile = imageFile;

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
//                    requestInfo.addData = new AddData();
//                    requestInfo.addData.content = group.content;
                    final String jsonObject = createJsonObject(group.personal);
                    httpManager.putRequest(path, jsonObject, constantConfig.POST_GROUP,
                            GroupsInteractor.this);
                } catch (Exception error) {
                    error(error);
                }
            }
        }).start();
    }

    public void getGroup (String id){
        String path = httpConfig.serverURL + httpConfig.SERVER_GETTER + httpConfig.reqGroup + "/" + id;
        startGetRequest(path, constantConfig.GET_GROUP_TYPE, this);
    }

    public void postSubscription(String id){
        Log.i("INTERACTOR", id);
        String path = httpConfig.serverURL + httpConfig.SERVER_SETTER + httpConfig.SUBSCRIPTION + httpConfig.reqGroup + "/" + id;
        startPostRequest(path, null, constantConfig.SUBSCRIPTION, this);
    }



    //--------------------------Получение данных из HTTP MANAGER и вызов функций обработки---------//

    @Override
    public void response(byte[] byteArray, String type) {
        if (type.equals(constantConfig.GET_GROUPS_TYPE)){
            prepareGetGroupsResponse(byteArray);
        } else if ((parsingStringType(type).length > 1) && (parsingStringType(type)[0]
                .equals(constantConfig.IMAGE_TYPE))){
            prepareGetBitmapOfByte(parsingStringType(type)[1], byteArray);
        } else if(type.equals(constantConfig.DELETE_GROUP)){
            delegate.answerDeleteGroups();
        } else if (type.equals(constantConfig.POST_GROUP)){
            answerAddGroup(byteArray);
        } else if (type.equals(constantConfig.GET_GROUP_TYPE)){
            prepareGetGroupResponse(byteArray);
        } else if (type.equals(constantConfig.SUBSCRIPTION)){
            preparePostSubcription(byteArray);
        } else if (type.equals(constantConfig.GET_GROUP_AFTER_EDIT_TYPE)){
            prepareGetGroupAfterEditResponse(byteArray);
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
    public void errorHanding(int responseCode, String type) {
        if (responseCode == 400){

        }
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
            final GroupsId groupsId = createGroupsOfBytes(byteArray);
            if (groupsId == null){
                delegate.error(new NullPointerException());
            } else {
                countGroups = groupsId.groups.size();
                for(int i = 0; i < groupsId.groups.size(); i++) {
                    getGroup(groupsId.groups.get(i));
                    postSubscription(groupsId.groups.get(i));
//                    Handler mainHandler = new Handler(Looper.getMainLooper());
//                    Runnable myRunnable = new Runnable() {
//                        @Override
//                        public void run() {
//                            delegate.answerGetGroups(groupCollection);
//                        }
//                    };
//
//                    mainHandler.post(myRunnable);


                }
                //getImageRequest(groupCollection);
            }
        }
    }

    private void answerAddGroup(byte[] byteArray){
        final Group group = createGroupOfBytes(byteArray);
        countGroups = 1;
        getGroup(group.id);
//        GroupDB groupDB = group.initGroupDB();
        postSubscription(group.id);
//        groupDataBase.groupDao().insert(groupDB);

        if (imageFile!=null){
            startPostImageGroup(group.id, imageFile);
        }
        String path = httpConfig.serverURL + httpConfig.SERVER_GETTER + httpConfig.reqGroup + "/" + group.id;
        startGetRequest(path, constantConfig.GET_GROUP_AFTER_EDIT_TYPE, this);
//        Handler mainHandler = new Handler(Looper.getMainLooper());
//        Runnable myRunnable = new Runnable() {
//            @Override
//            public void run() {
//                delegate.answerAddGroup(group);
//            }
//        };
//        mainHandler.post(myRunnable);

    }
    public void prepareGetGroupAfterEditResponse (byte[] byteArray) {
        final Group group = createGroupOfBytes(byteArray);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
//                    requestInfo.addData = new AddData();
//                    requestInfo.addData.content = group.content;
                    //запись в БД
                    GroupDB groupDB = group.initGroupDB();
                    //TODO: Эти группы уже есть в БД, поэтому шлет ошибку, надо включить проверку (если есть в БД, то не бегать на сервер или добавлять только те группы на которые есть подписки
                    groupDataBase.groupDao().insert(groupDB);
                } catch (Exception error) {
                    error(error);
                }
            }
        }).start();

        Handler mainHandler = new Handler(Looper.getMainLooper());
        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                delegate.answerAddGroup(group);
            }
        };
        mainHandler.post(myRunnable);

    }

    public void prepareGetGroupResponse (byte[] byteArray){
        final Group group = createGroupOfBytes(byteArray);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
//                    requestInfo.addData = new AddData();
//                    requestInfo.addData.content = group.content;
                    //запись в БД
                    GroupDB groupDB = group.initGroupDB();
                    //TODO: Эти группы уже есть в БД, поэтому шлет ошибку, надо включить проверку (если есть в БД, то не бегать на сервер или добавлять только те группы на которые есть подписки
                    groupDataBase.groupDao().insert(groupDB);
                } catch (Exception error) {
                    error(error);
                }
            }
        }).start();

//        getImageRequest(group.id);
        groups.add(group);
        if (countGroups == groups.size()){
            if(groups.size() > 1) {
                Collections.sort(groups, new Comparator<Group>() {
                    @Override
                    public int compare(Group o1, Group o2) {
                        return o1.personal.descriptive.title.compareTo(o2.personal.descriptive.title);
                    }
                });
            }
//            Collections.sort(groups);//TODO: сортировка не работает
            Handler mainHandler = new Handler(Looper.getMainLooper());
            Runnable myRunnable = new Runnable() {
                @Override
                public void run() {
                    delegate.answerGetGroups(groups);
                    groups = new ArrayList<>();//TODO для обновления списка при BackPressed, по ходу костыль, надо как-то подругому продумать
                }
            };
            mainHandler.post(myRunnable);
        }
    }

    private void preparePostSubcription (byte[] byteArray){
        if (byteArray != null) {
            Gson gson = new Gson();
            String jsonString = new String(byteArray);
            Log.i("TAG", jsonString);
        }
    }


    //================================SUPPORT METHODS======================================//

    private String[] parsingStringType(String string){//--------------------------------------------разбор строки (getImageGroupType + ":" + groupID)
        String delimiter = ":";
        return string.split(delimiter);
    }

    private GroupsId createGroupsOfBytes (byte[] byteArray){//----------------------создание массива групп из массива байтов
        Gson gson = new Gson();
        String jsonString = new String(byteArray);
        return gson.fromJson(jsonString, new TypeToken<GroupsId>(){}.getType());
    }

    private Group createGroupOfBytes (byte[] byteArray){//----------------------создание массива групп из массива байтов
        Gson gson = new Gson();
        String jsonString = new String(byteArray);
        return gson.fromJson(jsonString, new TypeToken<Group>(){}.getType());
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


    private String createJsonObject(Personal personal){
        Gson gson = new Gson();
//        if(bitmap != null){
//            requestInfo.addData.content.mediaData = new GroupMediaData();
//            requestInfo.addData.content.mediaData.image = decodeBitmapInBase64(bitmap);
//        }
        return gson.toJson(personal);
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

    private void startPostImageGroup(final String groupId, final File imageFile){

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    MultipartUtility multipartUtility = new MultipartUtility(httpConfig.serverURL + "9003" + httpConfig.GROUP + "/" + groupId + httpConfig.IMAGE, "UTF-8");
                    multipartUtility.addFilePart("file", imageFile);
                    List<String> response = multipartUtility.finish();
                    Log.e("TAGGGG", "SERVER REPLIED:");
                    for (String line : response) {
                        Log.e("TAAAAAGGGG", "Upload Files Response:::" + line);
                    }
                } catch (Exception error) {
                    error(error);
                }
            }
        }).start();

    }

    private void startPostRequest (final String path, final RequestInfo requestInfo,
                                   final String type, final OutputHTTPManagerInterface delegate){
        new Thread(new Runnable() {//---------------------------------------------------------------запуск в фоновом потоке
            @Override
            public void run() {
                try {
                    Gson gson = new Gson();
                    final String jsonObject = gson.toJson(requestInfo);
                    httpManager.postRequest(path, jsonObject, type, delegate);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}


