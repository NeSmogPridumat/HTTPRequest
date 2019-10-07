package com.dreamteam.httprequest.Group.Interactor;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.util.Calendar;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.util.Log;

import com.dreamteam.httprequest.Data.ConstantConfig;
import com.dreamteam.httprequest.Data.RequestInfo;
import com.dreamteam.httprequest.Event.Entity.EventType4.EventType4;
import com.dreamteam.httprequest.Event.Entity.Events.EventsObject;
import com.dreamteam.httprequest.Event.Entity.InfoStartEvent.InfoStartEvent;
import com.dreamteam.httprequest.Group.Entity.GroupData.EditGroupData.EditGroupData;
import com.dreamteam.httprequest.Group.Entity.GroupData.Group;
import com.dreamteam.httprequest.Group.Entity.GroupData.Invitation.ArrayInvitation;
import com.dreamteam.httprequest.Group.Entity.GroupData.Invitation.Invitation;
import com.dreamteam.httprequest.Group.Entity.GroupData.NodeData;
import com.dreamteam.httprequest.Group.Entity.GroupData.Personal;
import com.dreamteam.httprequest.Group.Protocols.GroupPresenterInterface;
import com.dreamteam.httprequest.Data.HTTPConfig;
import com.dreamteam.httprequest.GroupList.Data.AddGroupData;
import com.dreamteam.httprequest.HTTPManager.HTTPManager;
import com.dreamteam.httprequest.Interfaces.GroupHTTPMangerInterface;
import com.dreamteam.httprequest.Interfaces.OutputHTTPManagerInterface;
import com.dreamteam.httprequest.MultipartUtility;
import com.dreamteam.httprequest.ObjectList.ObjectData;
import com.dreamteam.httprequest.SelectedList.Data.SelectData;
import com.dreamteam.httprequest.User.Entity.UserData.User;
import com.dreamteam.httprequest.User.Entity.UserData.UsersId;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GroupInteractor implements GroupHTTPMangerInterface {

    private final static String TAG = "UserInteractor";

    private ConstantConfig constantConfig = new ConstantConfig();

    private GroupPresenterInterface delegate;
    private HTTPManager httpManager = HTTPManager.get();
    private HTTPConfig httpConfig = new HTTPConfig();
    private ArrayList<User> usersGroup = new ArrayList<>();
    private ArrayList<Group> getSubgroups = new ArrayList<>();
    private int countUsers;
    private ArrayList<User> users;
    private File imageFile;

    private String groupId;

    private int countSubgroup;

    public GroupInteractor(GroupPresenterInterface delegate) {
        this.delegate = delegate;
    }

    //======================================REQUESTS========================================//

    public void getGroup(String id, String userID) {
        final String path = httpConfig.serverURL + httpConfig.SERVER_GETTER + httpConfig.reqGroup + "/" + id;
                //+ httpConfig.GROUP_ID_PARAM + id + httpConfig.USER_ID_PARAM_2 + userID;

        startGetRequest(path, constantConfig.GET_GROUP_TYPE, GroupInteractor.this);
    }

    //метод отправки запроса
    private void getMembersRequest(Group group){
        users = new ArrayList<>();
        final String membersPath = httpConfig.serverURL + httpConfig.SERVER_GETTER
                + httpConfig.reqUser + httpConfig.reqGroup + httpConfig.GROUP_ID_PARAM + group.id;

        startGetRequest(membersPath, constantConfig.MEMBERS_TYPE, GroupInteractor.this);
    }

    public void getImageRequest(Group group) {//-----------------------------------------------------получение картинки
        ThreadLocal tl = new ThreadLocal();
        try {
            tl.set(System.nanoTime());
            String imageUrl = httpConfig.serverURL + httpConfig.SERVER_GETTER + httpConfig.GROUP + "/" + group.id + httpConfig.IMAGE;
//                    + group.content.mediaData.image;
            httpManager.getRequest(imageUrl, constantConfig.IMAGE_TYPE, GroupInteractor.this);
        }
        finally {
            tl.remove();
        }
    }

    public void getGroupAfterEdit(String id, String userID) {
        final String path = httpConfig.serverURL + httpConfig.SERVER_GETTER + httpConfig.reqGroup
                + httpConfig.GROUP_ID_PARAM + id + httpConfig.USER_ID_PARAM_2 + userID;

        startGetRequest(path, constantConfig.GET_GROUP_AFTER_EDIT_TYPE, GroupInteractor.this);
    }

    //запрос на получение списка юзеров для выбора админа
    public void checkListAddAdmin(ArrayList<String> members){
        countUsers = members.size();
        for(String i: members) {
            final String path = httpConfig.serverURL + httpConfig.SERVER_GETTER + httpConfig.USER + "/" + i;

            startGetRequest(path, constantConfig.ADMIN, GroupInteractor.this);
        }
    }

    //запрос на получение списка юзеров для удаления
    public void checkListDeleteUser(ArrayList<String> members){

        countUsers = members.size();
        for (String i : members) {
            final String membersPath = httpConfig.serverURL + httpConfig.SERVER_GETTER
                    + httpConfig.reqUser + "/" + i;
//                    httpConfig.reqGroup + httpConfig.GROUP_ID_PARAM + id;

            startGetRequest(membersPath, constantConfig.GET_USERS_FOR_SELECT_DELETE_TYPE, GroupInteractor.this);

        }
    }

    public void checkListAddUser(String groupID){
        users = new ArrayList<>();
        final String membersPath = httpConfig.serverURL + httpConfig.SERVER_GETTER
                + httpConfig.reqUser;
//                + httpConfig.reqGroup + httpConfig.GROUP_ID_PARAM + groupID;
//        final String path = httpConfig.serverURL + httpConfig.SERVER_GETTER + httpConfig.USER + "?name=111";
        startGetRequest(membersPath, constantConfig.GET_USERS_FOR_ADD_STEP_1_TYPE, GroupInteractor.this);
    }

    public void deleteSelectUser(final ArrayList<SelectData> selectData, final String groupId,
                                 final String userID) {

        EditGroupData editGroupData = new EditGroupData();
        editGroupData.members = new ArrayList<>();

        for (SelectData i : selectData) {
            editGroupData.members.add(i.id);
        }

        final String path = httpConfig.serverURL + httpConfig.SERVER_SETTER + httpConfig.GROUP + "/" + groupId;
//                + httpConfig.USER  + httpConfig.DEL;

        startPostRequest(path, editGroupData,constantConfig.SET_DELETE_USER_IN_GROUP_TYPE, GroupInteractor.this);

    }

    public void deleteGroup (String groupId){
        //собираем путь запроса
        final String path = httpConfig.serverURL + httpConfig.SERVER_SETTER + httpConfig.reqGroup + "/" + groupId;
//                + httpConfig.DEL;

        startDeleteRequest(path, constantConfig.DELETE_GROUP, GroupInteractor.this);
    }

    public void editGroupPost(final String groupId, final EditGroupData editGroupData, final File imageFile){
        final String path = httpConfig.serverURL + httpConfig.SERVER_SETTER + httpConfig.reqGroup + "/" + groupId;
        this.groupId = groupId;
        new Thread(new Runnable() {
            @Override
            public void run() {
            try {
                final String jsonObject = createJsonObject(editGroupData);
                httpManager.postRequest(path, jsonObject, constantConfig.POST_GROUP,
                        GroupInteractor.this);


                startPostImageGroup(groupId, imageFile);
            } catch (Exception error) {
                error(error);
            }
            }
        }).start();

    }

    public void addSubGroup(final Group group, File imageFile) {
        final String path = httpConfig.serverURL + httpConfig.SERVER_SETTER + httpConfig.reqGroup;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final String jsonObject = createJsonObject(group.personal);
                    httpManager.putRequest(path, jsonObject, constantConfig.POST_GROUP,
                            GroupInteractor.this);
                } catch (Exception error) {
                    error(error);
                }
            }
        }).start();
        this.imageFile = imageFile;

        //startPostImageGroup(groupId, imageFile);
    }

    public void getUserForList (ArrayList<String> members){
        users = new ArrayList<>();
        countUsers = members.size();
        for (String i : members) {
            final String membersPath = httpConfig.serverURL + httpConfig.SERVER_GETTER
                    + httpConfig.reqUser + "/" + i;
//                    httpConfig.reqGroup + httpConfig.GROUP_ID_PARAM + id;

            startGetRequest(membersPath, constantConfig.MEMBERS_FOR_LIST_TYPE,
                    GroupInteractor.this);
        }
    }

    public void exitGroup(String groupId, EditGroupData editGroupData){
        final String path = httpConfig.serverURL + httpConfig.SERVER_SETTER + httpConfig.GROUP + "/" + groupId;
//                        + httpConfig.USER + httpConfig.DEL;

        startPostRequest(path, editGroupData, constantConfig.EXIT_USER_IN_GROUP_TYPE,
                GroupInteractor.this);
    }

    public void startVoited(String groupId){
        final String path = httpConfig.serverURL + httpConfig.SERVER_SETTER + httpConfig.EVENT + "/" + constantConfig.EVENT_TYPE_RATING;
//                                                + httpConfig.VOITED;
        InfoStartEvent infoStartEvent = new InfoStartEvent();
        infoStartEvent.group = groupId;
        Date date = new Date();
        date.getTime();
        Log.i("ДАТА", date.toString());
        infoStartEvent.closingTime = System.currentTimeMillis() + 24*60;

        startPutRequest(path, infoStartEvent, constantConfig.VOITED, GroupInteractor.this);
    }

    public void startDiscussion(String groupId, String title, String message, String priority){
        final String path = httpConfig.serverURL + httpConfig.SERVER_SETTER + httpConfig.EVENT + "/" + constantConfig.EVENT_TYPE_DISCUSSION;
        InfoStartEvent infoStartEvent = new InfoStartEvent();
        infoStartEvent.group = groupId;
        Date date = new Date();
        date.getTime();
        Log.i("ДАТА", date.toString());
        infoStartEvent.closingTime = date.getTime() + 150000000L;
        infoStartEvent.text = message;
        infoStartEvent.title = title;
        infoStartEvent.priority = priority;

        startPutRequest(path, infoStartEvent, constantConfig.EVENT_TYPE_DISCUSSION, GroupInteractor.this);
    }


    public void addAdmin(final EditGroupData editGroupData, String groupId){
        final String path = httpConfig.serverURL + httpConfig.SERVER_SETTER
                                        + httpConfig.GROUP + "/" + groupId;

        startPostRequest(path, editGroupData, constantConfig.ADD_ADMIN, GroupInteractor.this);
    }

    public void getSubgroup(ArrayList<String> subgroups, String userID){
        countSubgroup = subgroups.size();
        for (String i : subgroups){
            final String path = httpConfig.serverURL + httpConfig.SERVER_GETTER + httpConfig.reqGroup + "/" + i;
//                    + httpConfig.GROUP_ID_PARAM + i + httpConfig.USER_ID_PARAM_2 + userID;

            startGetRequest(path, constantConfig.GET_SUB_GROUP_TYPE, GroupInteractor.this);
        }
    }

    public void getEvents (String groupId){
        String path = httpConfig.serverURL + httpConfig.SERVER_GETTER + httpConfig.EVENT + "?gid=" + groupId + "&status=active&expanded=true";
        startGetRequest(path, constantConfig.EVENTS_TYPE, this);
    }


    //----------------------------------------ПОЛУЧЕНИЕ ДАННЫХ ОТ HTTP MANAGER И ВЫЗОВ ФУНКЦИЙ ДЛЯ ОБРАБОТКИ-----------------------------//
    @Override
    public void response(byte[] byteArray, String type) {
        if (type.equals(constantConfig.GET_GROUP_TYPE)) {
            prepareGetGroupResponse(byteArray);
        }else if (type.equals(constantConfig.IMAGE_TYPE)) {
            prepareGetImageResponse(byteArray);
        } else if (type.equals(constantConfig.MEMBERS_TYPE)){
//            prepareGetMembersResponse(byteArray);
        }  else if (type.equals(constantConfig.SET_USER_IN_GROUP_TYPE) ||
                type.equals(constantConfig.SET_DELETE_USER_IN_GROUP_TYPE)){
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
        }else if (type.equals(constantConfig.GET_SUB_GROUP_TYPE)){
            prepareGetSubGroupResponse(byteArray);
        } else if (type.equals(constantConfig.USER_TYPE)){
            prepareGetUsersForAdd(byteArray);
        } else if (type.equals(constantConfig.EVENT_TYPE_DISCUSSION)){
            prepareStartDiscussion();
        } else if (type.equals(constantConfig.EVENTS_TYPE)){
            prepareGetEvents(byteArray);
        }
        //====================Steppers=============================================//
        else if (type.equals(constantConfig.GET_USERS_FOR_ADD_STEP_1_TYPE)){
            prepareGetUserForSelect(byteArray);
        } else if (type.equals(constantConfig.GET_USERS_FOR_ADD_STEP_2_TYPE)){
            prepareGetUserForAddSelect(byteArray);
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
    public void errorHanding(final int responseCode, final String type) {

        Handler mainHandler = new Handler(Looper.getMainLooper());
        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                delegate.errorHading(responseCode, type);
            }
        };
        mainHandler.post(myRunnable);
    }

    private void prepareStartDiscussion(){
        Handler mainHandler = new Handler(Looper.getMainLooper());
        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                delegate.answerStartDiscussion();
            }
        };
        mainHandler.post(myRunnable);

    }

    private void prepareGetGroupAfterEditResponse(byte[] byteArray) {//-----------------------------------------------получение json ответа, преобразование его в User и вывод в основной поток
        try {
            final Group group = createGroupOfBytes(byteArray);
            if (group == null) {
                delegate.error(new NullPointerException());
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
            group.personal = new Personal();
            group = createGroupOfBytes(byteArray);
            if (group == null) {
                delegate.error(new NullPointerException());
            }

            final Group finalGroup = group;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        //отправка запроса на получение картинки
                        getImageRequest(finalGroup);

                        //отправка запроса на получение количества участников в группе
                       // getMembersRequest(finalGroup);
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
//    private void prepareGetMembersResponse (byte[] byteArray){
//        final ArrayList<User> members = createMembersOfBytes(byteArray);
//
//        Handler mainHandler = new Handler(Looper.getMainLooper());
//        Runnable myRunnable = new Runnable() {
//            @Override
//            public void run() {
//                delegate.answerGetMembers(members, "");
//            }
//        };
//        mainHandler.post(myRunnable);
//    }

    //получение списка членов группы для отображения в списке
    private void prepareGetMembersForListResponse (byte[] byteArray){
        User member = createUserOfBytes(byteArray);
        users.add(member);
        if (users.size() == countUsers){
            delegate.answerGetMembersForList(users);
        }
//        objectDataArrayList = new ArrayList<>();
//        for (User user : members){
//            //добавляем в коллекцию созданный в методе getObjectDataForUser() ObjectData
//            objectDataArrayList.add(getObjectDataForUser(user));
//        }
//        final ArrayList<ObjectData> objectDataArrayListFinal = objectDataArrayList;
//        Handler mainHandler = new Handler(Looper.getMainLooper());
//        Runnable myRunnable = new Runnable() {
//            @Override
//            public void run() {
//                delegate.answerGetMembersForList(objectDataArrayListFinal);
//            }
//        };
//        mainHandler.post(myRunnable);
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
                delegate.backPressAfterSelectAdmin();
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

    private void prepareAddSubgroupResponse (byte[] byteArray){
        try {

            final AddGroupData addGroupData = createEventOfBytes(byteArray);

            if (addGroupData == null) {
                delegate.error(new NullPointerException());
            }
            String groupID = addGroupData.id;
            if(this.groupId != null){
                groupID = groupId;
            }
            if (imageFile != null) {
                startPostImageGroup(groupID, imageFile);
            }
            Handler mainHandler = new Handler(Looper.getMainLooper());
            final String finalGroupID = groupID;
            Runnable myRunnable = new Runnable() {
                @Override
                public void run() {
                    delegate.answerAddGroup(finalGroupID);
                }
            };
            mainHandler.post(myRunnable);

            Thread.currentThread().interrupted();
        } catch (Exception error) {
            error(error);
        }
    }

    private void prepareGetUserForSelect (byte[] byteArray){
        //usersGroup = createMembersOfBytes(byteArray);
        UsersId usersId = createMembersOfBytes(byteArray);
        countUsers = usersId.users.size();
        for(int i = 0; i < countUsers; i++){
            final String path = httpConfig.serverURL + httpConfig.SERVER_GETTER + httpConfig.USER + "/" + usersId.users.get(i);
            startGetRequest(path, constantConfig.USER_TYPE, this);
        }
//        final String path = httpConfig.serverURL + httpConfig.SERVER_GETTER + httpConfig.USER + "/?name=666";
//        httpManager.getRequest(path, constantConfig.GET_USERS_FOR_ADD_STEP_2_TYPE,
//                GroupInteractor.this);//----------отправка в HTTPManager
    }

    private void prepareGetUserForAddSelect (byte[] bytes){
        User userGet = createUserOfBytes(bytes);
        users.add(userGet);
        if(countUsers == users.size()) {
//        ArrayList<User> allUsers = createUserOfBytes(bytes);
            for (User user : users) {
                for (int i = 0; i < users.size(); i++) {
                    if (user.id.equals(users.get(i).id)) {
                        users.remove(i);
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
    }

    private void prepareGetUserForSelectAdmin(byte[] byteArray){
        User user = createUserOfBytes(byteArray);
        users.add(user);
        if (users.size() == countUsers) {
            //final ArrayList<User> users = createMembersOfBytes(byteArray);
            Handler mainHandler = new Handler(Looper.getMainLooper());
            Runnable myRunnable = new Runnable() {
                @Override
                public void run() {
                    delegate.answerGetUsersForSelectAdmin(users);
                }
            };
            mainHandler.post(myRunnable);
        }
    }

    public void addSelectUser(ArrayList<SelectData> arrayList, String groupId, String userID){
        final String path = httpConfig.serverURL + httpConfig.SERVER_SETTER
//                + httpConfig.GROUP + httpConfig.USER + httpConfig.ADD;
                + httpConfig.INVITATION;
        ArrayInvitation arrayInvitation = getArrayInvitation(arrayList, groupId);


        startPutRequest(path, arrayInvitation, constantConfig.SET_USER_IN_GROUP_TYPE,
                GroupInteractor.this);

    }

    private ArrayInvitation getArrayInvitation(ArrayList<SelectData> arrayList, String groupId){
        ArrayInvitation arrayInvitation = new ArrayInvitation();
        for (SelectData selectData : arrayList) {
            Invitation invitation = new Invitation();
            invitation.group = groupId;
            invitation.receiver = selectData.id;
            arrayInvitation.invitations.add(invitation);
        }
        return arrayInvitation;
    }

    private void prepareGetSubGroupResponse(byte[] byteArray){
        Group group = new Group();
        group.nodeData = new NodeData();
        group = createGroupOfBytes(byteArray);
        if (group == null) {
            delegate.error(new NullPointerException());
        }
        getSubgroups.add(group);
        if(getSubgroups.size() == countSubgroup){
            Handler mainHandler = new Handler(Looper.getMainLooper());
            Runnable myRunnable = new Runnable() {
                @Override
                public void run() {
                    delegate.answerGetSubgroup(getSubgroups);
                    getSubgroups.clear();
                    countSubgroup = 0;
                }
            };
            mainHandler.post(myRunnable);
        }
    }

    private void prepareGetDeleteUserForSelect (byte[] byteArray){

        User member = createUserOfBytes(byteArray);
        users.add(member);
        if (users.size() == countUsers){

            Handler mainHandler = new Handler(Looper.getMainLooper());
            Runnable myRunnable = new Runnable() {
                @Override
                public void run() {
                    delegate.answerGetUsersForSelectDelete(users);
                }
            };
            mainHandler.post(myRunnable);
        }
    }


    private void prepareGetUsersForAdd (byte[] byteArray){
        User user = createUserOfBytes(byteArray);
        users.add(user);
        if (users.size() == countUsers){
            Handler mainHandler = new Handler(Looper.getMainLooper());
            Runnable myRunnable = new Runnable() {
                @Override
                public void run() {
                    delegate.answerGetUsersForSelectAdd(users);
                }
            };
            mainHandler.post(myRunnable);
        }
    }

    private void prepareGetEvents (byte[] byteArray){
        EventsObject eventObject = createEventsOfBytes(byteArray);
        delegate.answerGetEvents(eventObject);
    }

    //=============================SUPPORT METHODS=============================================//

    private Group createGroupOfBytes(byte[] byteArray) {//---------------------------------------------создание User из массива байтов
        Gson gson = new Gson();
        String jsonString = new String(byteArray);
        return gson.fromJson(jsonString, Group.class);
    }


    private AddGroupData createEventOfBytes(byte[] byteArray) {//---------------------------------------------создание User из массива байтов
        Gson gson = new Gson();
        String jsonString = new String(byteArray);
        return gson.fromJson(jsonString, AddGroupData.class);
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

    //post-запросы на сервер
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

    //post-запросы на сервер
    private void startPostRequest (final String path, final EditGroupData editGroupData,
                                   final String type, final OutputHTTPManagerInterface delegate){
        new Thread(new Runnable() {//---------------------------------------------------------------запуск в фоновом потоке
            @Override
            public void run() {
                try {
                    Gson gson = new Gson();
                    final String jsonObject = gson.toJson(editGroupData);
                    httpManager.postRequest(path, jsonObject, type, delegate);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void startPutRequest (final String path, final InfoStartEvent infoStartEvent,
                                   final String type, final OutputHTTPManagerInterface delegate){
        new Thread(new Runnable() {//---------------------------------------------------------------запуск в фоновом потоке
            @Override
            public void run() {
                try {
                    Gson gson = new Gson();
                    final String jsonObject = gson.toJson(infoStartEvent);
                    httpManager.putRequest(path, jsonObject, type, delegate);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
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

    private void startDeleteRequest(final String path, final String type,
                                    final OutputHTTPManagerInterface delegate){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    httpManager.delRequest(path, type, delegate);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private String decodeBitmapInBase64 (Bitmap bitmap){//------------------------------------------декодирование Bitmap в Base64
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);//поставил 50, потому что долго грузит большие картинки
        // Получаем изображение из потока в виде байтов
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return constantConfig.PREFIX + Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    private String createJsonObject(Personal personal){
        Gson gson = new Gson();
//        if(bitmap != null){
//            requestInfo.addData.content.mediaData = new GroupMediaData();
//            requestInfo.addData.content.mediaData.image = decodeBitmapInBase64(bitmap);
//        }
        return gson.toJson(personal);
    }


    private String createJsonObject(EditGroupData editGroupData){
        Gson gson = new Gson();
//        if(bitmap != null){
//            requestInfo.addData.content.mediaData = new GroupMediaData();
//            requestInfo.addData.content.mediaData.image = decodeBitmapInBase64(bitmap);
//        }
        return gson.toJson(editGroupData);
    }

    private UsersId createMembersOfBytes(byte[] byteArray){
        Gson gson = new Gson();
        String jsonString = new String(byteArray);
        return gson.fromJson(jsonString, new TypeToken<UsersId>(){}.getType());
    }

    private EventsObject createEventsOfBytes (byte[] byteArray){

        Gson gson = new Gson();
        String jsonString = new String(byteArray);
        return gson.fromJson(jsonString, new TypeToken<EventsObject>() {}.getType());
    }

    private RequestInfo getRequestInfoForSelectData (SelectData selectData, String groupId, String userID){
        RequestInfo requestInfo = new RequestInfo();
        requestInfo.userID = selectData.id;
        requestInfo.groupID = groupId;
        requestInfo.groupCreatorID = groupId;
        requestInfo.creatorID = userID;
        return  requestInfo;
    }

    private ObjectData getObjectDataForUser (User user){
        ObjectData objectData = new ObjectData();
        objectData.id = user.id;
        objectData.title = user.personal.descriptive.name;
        objectData.description = user.personal.descriptive.surname;
        //objectData.image = user.content.mediaData.image;
        //objectData.rules = user.rules;
        return objectData;
    }


    private User createUserOfBytes(byte[] byteArray) {
        Gson gson = new Gson();
        String jsonString = new String(byteArray);
        return gson.fromJson(jsonString, User.class);
    }

    private void startPutRequest (final String path, final ArrayInvitation arrayInvitation,
                                  final String type, final OutputHTTPManagerInterface delegate){
        new Thread(new Runnable() {//---------------------------------------------------------------запуск в фоновом потоке
            @Override
            public void run() {
                try {
                    Gson gson = new Gson();
                    final String jsonObject = gson.toJson(arrayInvitation);
                    httpManager.putRequest(path, jsonObject, type, delegate);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }



    //Запрос на изменение аватара группы
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
                    Log.e("TAAAAAGGGG", "Upload Files Response:" + line);
                }

            } catch (Exception error) {
                error(error);
            }
            }
        }).start();

    }
}
