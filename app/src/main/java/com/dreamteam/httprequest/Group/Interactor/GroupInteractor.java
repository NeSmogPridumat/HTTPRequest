package com.dreamteam.httprequest.Group.Interactor;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.util.Log;

import com.dreamteam.httprequest.Data.ConstantConfig;
import com.dreamteam.httprequest.Data.RequestInfo;
import com.dreamteam.httprequest.Event.Entity.EventType4.EventType4;
import com.dreamteam.httprequest.Group.Entity.GroupData.Group;
import com.dreamteam.httprequest.Group.Entity.GroupData.GroupMediaData;
import com.dreamteam.httprequest.Group.Entity.GroupData.NodeData;
import com.dreamteam.httprequest.Group.Protocols.GroupPresenterInterface;
import com.dreamteam.httprequest.HTTPConfig;
import com.dreamteam.httprequest.HTTPManager;
import com.dreamteam.httprequest.Interfaces.GroupHTTPMangerInterface;
import com.dreamteam.httprequest.ObjectList.ObjectData;
import com.dreamteam.httprequest.SelectedList.SelectData;
import com.dreamteam.httprequest.User.Entity.UserData.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

public class GroupInteractor implements GroupHTTPMangerInterface {

    private final static String TAG = "UserInteractor";

    private ConstantConfig constantConfig = new ConstantConfig();

    //===========================КОНСТАНТЫ ДЛЯ ТИПОВ ЗАПРОСА===================================//

    private GroupPresenterInterface delegate;
    private HTTPManager httpManager = HTTPManager.get();
    private HTTPConfig httpConfig = new HTTPConfig();
    private ArrayList<User> usersGroup = new ArrayList<>();

    public GroupInteractor(GroupPresenterInterface delegate) {
        this.delegate = delegate;
    }


    //----------------------------------------ПОЛУЧЕНИЕ ДАННЫХ ОТ HTTP MANAGER И ВЫЗОВ ФУНКЦИЙ ДЛЯ ОБРАБОТКИ-----------------------------//
    @Override
    public void response(byte[] byteArray, String type) {
        if (type.equals(constantConfig.GET_GROUP_TYPE)) {
            prepareGetGroupResponse(byteArray);
        }else if (type.equals(constantConfig.IMAGE_TYPE)) {
            prepareGetImageResponse(byteArray);
        } else if (type.equals(constantConfig.MEMBERS_TYPE)){
            prepareGetMembersResponse(byteArray);
        }  else if (type.equals(constantConfig.SET_USER_IN_GROUP_TYPE) || type.equals(constantConfig.SET_DELETE_USER_IN_GROUP_TYPE)){
            delegate.openGroupAfterSelect();
        } else if(type.equals(constantConfig.GET_USERS_FOR_SELECT_DELETE_TYPE)){
            prepareGetDeleteUserForSelect(byteArray);
        } else if (type.equals(constantConfig.DELETE_GROUP)){
            prepareDeleteGroup();
        } else if (type.equals(constantConfig.POST_GROUP)){
            prepareAddSubgroupResponse(byteArray);
        } else if (type.equals(constantConfig.ADMIN)){
            prepareGetUserForSelectAdmin(byteArray);
        } else if (type.equals(constantConfig.MEMBERS_FOR_LIST_TYPE)){
            prepareGetMembersForListResponse(byteArray);
        } else if (type.equals(constantConfig.EXIT_USER_IN_GROUP_TYPE)){
            backPress();
        }else if (type.equals(constantConfig.VOITED)){
            prepareStartVoited();
        }else if (type.equals(constantConfig.GET_GROUP_AFTER_EDIT_TYPE)){
            prepareGetGroupAfterEditResponse(byteArray);
        } else if (type.equals(constantConfig.ADD_ADMIN)){
            prepareAddAdminResponse(byteArray);
        }
        //====================Steppers=============================================//
        else if (type.equals(constantConfig.GET_USERS_FOR_ADD_STEP_1_TYPE)){
            prepareGetUserForSelect(byteArray);
        } else if (type.equals(constantConfig.GET_USERS_FOR_ADD_STEP_2_TYPE)){
            prepareGetUserForAddSelect(byteArray);
        }
    }

    public void getGroup(String id, String userID) {
        final String path = httpConfig.serverURL
            + httpConfig.SERVER_GETTER
            + httpConfig.reqGroup
            + httpConfig.GROUP_ID_PARAM
            + id
            + httpConfig.USER_ID_PARAM_2
            + userID;
        new Thread(
            new Runnable() {//---------------------------------------------------------------запуск в фоновом потоке
                @Override
                public void run() {
                    httpManager.getRequest(path, constantConfig.GET_GROUP_TYPE,
                        GroupInteractor.this);//-------------------------------------------отправка в HTTPManager
                }
            }).start();
    }

    private void prepareGetGroupAfterEditResponse(byte[] byteArray) {//-----------------------------------------------получение json ответа, преобразование его в User и вывод в основной поток
        try {
            final Group group = createGroupOfBytes(byteArray);
            if (group == null) {
                String error = "Объект не существует";
                delegate.error(error, null);
            }

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        //отправка запроса на получение картинки
                        getImageRequest(group);

                        //отправка запроса на получение количества участников в группе
                        getMembersRequest(group);
                    } catch (Exception error) {
                        error(error);
                    }
                }
            }).start();
            Handler mainHandler = new Handler(Looper.getMainLooper());
            Runnable myRunnable = new Runnable() {
                @Override
                public void run() {
                    delegate.answerGetGroupAfterEdit(group);
                }
            };
            mainHandler.post(myRunnable);
        } catch (Exception error) {
            error(error);
        }
    }

    private void prepareGetGroupResponse(byte[] byteArray) {//-----------------------------------------------получение json ответа, преобразование его в User и вывод в основной поток
        try {
            Group group = new Group();
            group.nodeData = new NodeData();
            group = createGroupOfBytes(byteArray);
            if (group == null) {
                String error = "Объект не существует";
                delegate.error(error, null);
            }

            final Group finalGroup = group;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        //отправка запроса на получение картинки
                        getImageRequest(finalGroup);

                        //отправка запроса на получение количества участников в группе
                        getMembersRequest(finalGroup);
                    } catch (Exception error) {
                        error(error);
                    }
                }
            }).start();
            Handler mainHandler = new Handler(Looper.getMainLooper());
            Runnable myRunnable = new Runnable() {
                @Override
                public void run() {
                delegate.answerGetGroup(finalGroup);
                }
            };
            mainHandler.post(myRunnable);
        } catch (Exception error) {
            error(error);
        }
    }

    //метод отправки запроса
    private void getMembersRequest(Group group){
        final String membersPath = httpConfig.serverURL + httpConfig.SERVER_GETTER + httpConfig.reqUser + httpConfig.reqGroup + httpConfig.GROUP_ID_PARAM + group.id; //"5c4a0105-c5b5-450e-b781-113a21f16e5a"

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    httpManager.getRequest(membersPath, constantConfig.MEMBERS_TYPE, GroupInteractor.this);
                } catch (Exception error) {
                    error(error);
                }
            }
        }).start();
    }

    private void getImageRequest(Group group) {//-----------------------------------------------------получение картинки
        ThreadLocal tl = new ThreadLocal();
        try {
            tl.set(System.nanoTime());
            String imageUrl = httpConfig.serverURL + httpConfig.SERVER_GETTER + group.content.mediaData.image;
            httpManager.getRequest(imageUrl, constantConfig.IMAGE_TYPE, GroupInteractor.this);
        }
        finally {
            tl.remove();
        }
    }

    private Group createGroupOfBytes(byte[] byteArray) {//---------------------------------------------создание User из массива байтов
        Gson gson = new Gson();
        String jsonString = new String(byteArray);
        Log.i("wqe", "wqe");
        return gson.fromJson(jsonString, Group.class);
    }

    private EventType4 createEventOfBytes(byte[] byteArray) {//---------------------------------------------создание User из массива байтов
        Gson gson = new Gson();
        String jsonString = new String(byteArray);
        Log.i("wqe", "wqe");
        return gson.fromJson(jsonString, EventType4.class);
    }

    private void prepareGetImageResponse(byte[] byteArray) {//------------------------------------------------------получение картинки(преобразование в bitmap)
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
        }
    }

    //получение списка членов группы
    private void prepareGetMembersResponse (byte[] byteArray){
        final ArrayList<User> members = createMembersOfBytes(byteArray);

        Handler mainHandler = new Handler(Looper.getMainLooper());
        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                delegate.answerGetMembers(members, "");
            }
        };
        mainHandler.post(myRunnable);
    }

    private void prepareGetMembersForListResponse (byte[] byteArray){
        final ArrayList<User> members = createMembersOfBytes(byteArray);
            ArrayList<ObjectData> objectDataArrayList = new ArrayList<>();
            for (int i = 0; i < members.size(); i++){
                ObjectData objectData = new ObjectData();
                objectData.id = members.get(i).id;
                objectData.title = members.get(i).content.simpleData.name;
                objectData.description = members.get(i).content.simpleData.surname;
                objectData.image = members.get(i).content.mediaData.image;
                objectDataArrayList.add(objectData);
            }
        final ArrayList<ObjectData> objectDataArrayListFinal = objectDataArrayList;
        Handler mainHandler = new Handler(Looper.getMainLooper());
        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                delegate.answerGetMembersForList(objectDataArrayListFinal);
            }
        };
        mainHandler.post(myRunnable);
    }

    private ArrayList<User> createMembersOfBytes(byte[] byteArray){
        Gson gson = new Gson();
        String jsonString = new String(byteArray);
        return gson.fromJson(jsonString, new TypeToken<ArrayList<User>>(){}.getType());
    }

    private void prepareDeleteGroup(){
        Handler mainHandler = new Handler(Looper.getMainLooper());
        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                delegate.openGroupsList();
            }
        };
        mainHandler.post(myRunnable);
    }

    private void prepareAddAdminResponse(byte[] byteArray){
        Handler mainHandler = new Handler(Looper.getMainLooper());
        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                delegate.backPress();
            }
        };
        mainHandler.post(myRunnable);
    }

    private void prepareStartVoited(){
        Handler mainHandler = new Handler(Looper.getMainLooper());
        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                delegate.answerStartVoited();
            }
        };
        mainHandler.post(myRunnable);
    }

    private void backPress(){
        Handler mainHandler = new Handler(Looper.getMainLooper());
        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                delegate.backPress();
            }
        };
        mainHandler.post(myRunnable);
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
    public void errorHanding(int resposeCode) {

    }

    public void getGroupAfterEdit(String id, String userID) {
        final String path = httpConfig.serverURL
                + httpConfig.SERVER_GETTER
                + httpConfig.reqGroup
                + httpConfig.GROUP_ID_PARAM
                + id
                + httpConfig.USER_ID_PARAM_2
                + userID;
        new Thread(
                new Runnable() {//---------------------------------------------------------------запуск в фоновом потоке
                    @Override
                    public void run() {
                        httpManager.getRequest(path, constantConfig.GET_GROUP_AFTER_EDIT_TYPE,
                                GroupInteractor.this);//----------отправка в HTTPManager
                    }
                }).start();
    }

    private void prepareAddSubgroupResponse (byte[] byteArray){
        try {
            final EventType4 event = createEventOfBytes(byteArray);
            if (event == null) {
                String error = "Объект не существует";
                delegate.error(error, null);
            }

            Handler mainHandler = new Handler(Looper.getMainLooper());
            Runnable myRunnable = new Runnable() {
                @Override
                public void run() {
                    delegate.answerAddGroup(event);
                }
            };
            mainHandler.post(myRunnable);

            Thread.currentThread().interrupted();
        } catch (Exception error) {
            error(error);
        }
    }

    public void checkListAddAdmin(){
        final String path = httpConfig.serverURL + httpConfig.SERVER_GETTER + httpConfig.USERS;
        new Thread(new Runnable() {//---------------------------------------------------------------запуск в фоновом потоке
            @Override
            public void run() {
                httpManager.getRequest(path, constantConfig.ADMIN, GroupInteractor.this);//----------отправка в HTTPManager
            }
        }).start();
    }

    public void checkListDeleteUser(String groupID){
        final String path = httpConfig.serverURL + httpConfig.SERVER_GETTER + httpConfig.reqUser + httpConfig.reqGroup + httpConfig.GROUP_ID_PARAM + groupID; //"5c4a0105-c5b5-450e-b781-113a21f16e5a"

        new Thread(new Runnable() {//---------------------------------------------------------------запуск в фоновом потоке
            @Override
            public void run() {
                httpManager.getRequest(path, constantConfig.GET_USERS_FOR_SELECT_DELETE_TYPE, GroupInteractor.this);
            }
        }).start();
    }

    //==============================================GET USERS FOR ADD IN GROUP============================================//
    public void checkListAddUser(String groupID){
        final String membersPath = httpConfig.serverURL + httpConfig.SERVER_GETTER + httpConfig.reqUser + httpConfig.reqGroup + httpConfig.GROUP_ID_PARAM + groupID; //"5c4a0105-c5b5-450e-b781-113a21f16e5a"

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    httpManager.getRequest(membersPath, constantConfig.GET_USERS_FOR_ADD_STEP_1_TYPE, GroupInteractor.this);
                } catch (Exception error) {
                    error(error);
                }
            }
        }).start();
    }

    private void prepareGetUserForSelect (byte[] byteArray){////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        usersGroup = createMembersOfBytes(byteArray);
        final String path = httpConfig.serverURL + httpConfig.SERVER_GETTER + httpConfig.USERS;
        httpManager.getRequest(path, constantConfig.GET_USERS_FOR_ADD_STEP_2_TYPE, GroupInteractor.this);//----------отправка в HTTPManager
    }

    private void prepareGetUserForAddSelect (byte[] bytes){
        ArrayList<User> allUsers = createMembersOfBytes(bytes);
        ArrayList<User> users = allUsers;
        for (int i = 0; i < allUsers.size(); i++){
            for (int j = 0; j < usersGroup.size(); j++){
                if (allUsers.get(i).id.equals(usersGroup.get(j).id)){
                    users.remove(allUsers.get(i));
                }
            }
        }
        final ArrayList<User> usersFinal = users;
        Handler mainHandler = new Handler(Looper.getMainLooper());
        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                delegate.answerGetUsersForSelectAdd(usersFinal);
            }
        };
        mainHandler.post(myRunnable);
    }
    //==============================================GET USERS FOR ADD IN GROUP============================================//

    private void prepareGetUserForSelectAdmin(byte[] byteArray){
        final ArrayList<User> users = createMembersOfBytes(byteArray);
        Handler mainHandler = new Handler(Looper.getMainLooper());
        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                delegate.answerGetUsersForSelectAdmin(users);

            }
        };
        mainHandler.post(myRunnable);
    }

    private void prepareGetDeleteUserForSelect (byte[] byteArray){
        final ArrayList<User> users = createMembersOfBytes(byteArray);
        Handler mainHandler = new Handler(Looper.getMainLooper());
        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                delegate.answerGetUsersForSelectDelete(users);

            }
        };
        mainHandler.post(myRunnable);
    }

    public void addSelectUser(ArrayList<SelectData> arrayList, String groupId, String userID){

        final String path = httpConfig.serverURL + httpConfig.SERVER_SETTER
                + httpConfig.GROUP + httpConfig.USER + httpConfig.ADD;
        for (int i = 0; i< arrayList.size(); i++){
            Gson gson = new Gson();
            RequestInfo requestInfo = new RequestInfo();
            requestInfo.userID = arrayList.get(i).id;
            requestInfo.groupID = groupId;
            requestInfo.groupCreatorID = groupId;
            requestInfo.creatorID = userID;

            final String jsonObject = gson.toJson(requestInfo);
            new Thread(new Runnable() {//---------------------------------------------------------------запуск в фоновом потоке
                @Override
                public void run() {
                    try {
                        httpManager.postRequest(path, jsonObject, constantConfig.SET_USER_IN_GROUP_TYPE, GroupInteractor.this);//----------отправка в HTTPManager
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    public void deleteSelectUser(final ArrayList<SelectData> selectData, final String groupId, final String userID) {
        final String path = httpConfig.serverURL + httpConfig.SERVER_SETTER + httpConfig.GROUP + httpConfig.USER  + httpConfig.DEL;

        new Thread(new Runnable() {//---------------------------------------------------------------запуск в фоновом потоке
            @Override
            public void run() {
                try {
                    for (int i = 0; i < selectData.size(); i++) {
                        Gson gson = new Gson();
                        RequestInfo requestInfo = new RequestInfo();
                        requestInfo.userID = selectData.get(i).id;
                        requestInfo.groupID = groupId;
                        requestInfo.groupCreatorID = groupId;
                        requestInfo.creatorID = userID;
                        String jsonObject = gson.toJson(requestInfo);
                        httpManager.postRequest(path, jsonObject, constantConfig.SET_DELETE_USER_IN_GROUP_TYPE,
                            GroupInteractor.this);//----------отправка в HTTPManager
                   }
                }catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void deleteGroup (RequestInfo requestInfo){
        //собираем путь запроса
        final String path = httpConfig.serverURL + httpConfig.SERVER_SETTER + httpConfig.reqGroup + httpConfig.DEL;//TODO
        Gson gson = new Gson();
        final String jsonObject = gson.toJson(requestInfo);
        new Thread(new Runnable() {//---------------------------------------------------------------запуск в фоновом потоке
            @Override
            public void run() {
        try {
            httpManager.postRequest(path, jsonObject, constantConfig.DELETE_GROUP, GroupInteractor.this);
        } catch (IOException e) {
            e.printStackTrace();
        }
            }
        }).start();

    }

    public void editGroupPut(final Bitmap bitmap, final RequestInfo requestInfo){
        final String path = httpConfig.serverURL + httpConfig.SERVER_SETTER + httpConfig.reqGroup;

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
//                    requestInfo.addData.content = group.content;
                    final String jsonObject = createJsonObject(bitmap, requestInfo);
                    httpManager.putRequest(path, jsonObject, constantConfig.POST_GROUP, GroupInteractor.this);
                } catch (Exception error) {
                    error(error);
                }
            }
        }).start();
    }

    public void addSubGroup(final Bitmap bitmap, final RequestInfo requestInfo) {
        final String path = httpConfig.serverURL + httpConfig.SERVER_SETTER + httpConfig.reqGroup;

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
//                    requestInfo.addData.content = group.content;
                    final String jsonObject = createJsonObject(bitmap, requestInfo);
                    httpManager.postRequest(path, jsonObject, constantConfig.POST_GROUP, GroupInteractor.this);
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
        String jsonRequestInfo = gson.toJson(requestInfo);
        return jsonRequestInfo;
    }

    private String decodeBitmapInBase64 (Bitmap bitmap){//------------------------------------------декодирование Bitmap в Base64
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);//TODO: поставил 50, потому что долго грузит большие картинки
        // Получаем изображение из потока в виде байтов
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return constantConfig.PREFIX + Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    public void getUserForList (String id){
        final String membersPath = httpConfig.serverURL + httpConfig.SERVER_GETTER + httpConfig.reqUser + httpConfig.reqGroup + httpConfig.GROUP_ID_PARAM + id; //"5c4a0105-c5b5-450e-b781-113a21f16e5a"

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    httpManager.getRequest(membersPath, constantConfig.MEMBERS_FOR_LIST_TYPE, GroupInteractor.this);
                } catch (Exception error) {
                    error(error);
                }
            }
        }).start();
    }

    public void exitGroup(RequestInfo requestInfo){
        final String path = httpConfig.serverURL + httpConfig.SERVER_SETTER + httpConfig.GROUP + httpConfig.DEL;

        Gson gson = new Gson();
        final String jsonObject = gson.toJson(requestInfo);

        new Thread(new Runnable() {//---------------------------------------------------------------запуск в фоновом потоке
            @Override
            public void run() {
                try {
                    httpManager.postRequest(path, jsonObject, constantConfig.EXIT_USER_IN_GROUP_TYPE,
                        GroupInteractor.this);//----------отправка в HTTPManager
                }catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void startVoited(final RequestInfo requestInfo){
        final String path = httpConfig.serverURL + httpConfig.SERVER_SETTER + httpConfig.GROUP + httpConfig.VOITED;
        new Thread(new Runnable() {//---------------------------------------------------------------запуск в фоновом потоке
            @Override
            public void run() {
                Gson gson = new Gson();
                String jsonObject = gson.toJson(requestInfo);
                try {
                    httpManager.postRequest(path, jsonObject, constantConfig.VOITED,
                            GroupInteractor.this);//----------отправка в HTTPManager
                }catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void addAdmin(final RequestInfo requestInfo){
        final String path = httpConfig.serverURL + httpConfig.SERVER_SETTER + httpConfig.RULES + httpConfig.GROUP
                + httpConfig.USER;
        new Thread(new Runnable() {//---------------------------------------------------------------запуск в фоновом потоке
            @Override
            public void run() {
                Gson gson = new Gson();
                String jsonObject = gson.toJson(requestInfo);
                try {
                    httpManager.postRequest(path, jsonObject, constantConfig.ADD_ADMIN,
                            GroupInteractor.this);//----------отправка в HTTPManager
                }catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
