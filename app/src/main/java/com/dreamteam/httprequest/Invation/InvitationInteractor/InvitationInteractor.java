package com.dreamteam.httprequest.Invation.InvitationInteractor;

import android.content.Context;
import android.view.View;

import com.dreamteam.httprequest.Data.ConstantConfig;
import com.dreamteam.httprequest.Data.HTTPConfig;
import com.dreamteam.httprequest.Group.Entity.GroupData.Group;
import com.dreamteam.httprequest.Group.Entity.GroupData.NodeData;
import com.dreamteam.httprequest.Group.Entity.GroupData.Personal;
import com.dreamteam.httprequest.HTTPManager.HTTPManager;
import com.dreamteam.httprequest.Interfaces.OutputHTTPManagerInterface;
import com.dreamteam.httprequest.Invation.Entity.Invitation.Invitation;
import com.dreamteam.httprequest.Invation.Protocols.InvationHTTPManagerInterface;
import com.dreamteam.httprequest.QueryPreferences;
import com.dreamteam.httprequest.Service.ForServiceInterface;
import com.dreamteam.httprequest.User.Entity.UserData.User;
import com.dreamteam.httprequest.database.App;
import com.dreamteam.httprequest.database.Data.GroupDB;
import com.dreamteam.httprequest.database.Data.InvitationDB;
import com.dreamteam.httprequest.database.Group.GroupDataBase;
import com.dreamteam.httprequest.database.Invitation.InvitationDataBase;
import com.dreamteam.httprequest.database.NoActiveGroup.NoActiveGroupDataBase;
import com.google.gson.Gson;

public class InvitationInteractor implements InvationHTTPManagerInterface {
    private HTTPManager httpManager = HTTPManager.get();
    private HTTPConfig httpConfig = new HTTPConfig();
    private ConstantConfig constantConfig = new ConstantConfig();
    private InvitationDataBase invitationDataBase = App.getInstance().getInvitationDataBase();
    private GroupDataBase groupDataBase = App.getInstance().getGroupDataBase();
    private NoActiveGroupDataBase noActiveGroupDataBase = App.getInstance().getNoActiveGroupDataBase();
    private Context context;
    private int count;
    private Invitation invitation;
    private ForServiceInterface delegate;


    public InvitationInteractor(Context context, ForServiceInterface delegate){
        this.context = context;
        this.delegate = delegate;
    }

    public void getInvation(String id){
        count = 0;
        String path = httpConfig.serverURL + httpConfig.SERVER_GETTER + httpConfig.INVITATION + "/" + id;
        startGetRequest(path, constantConfig.GET_INVITATION_TYPE, this);
    }

    public void ratingGroup(String id){
        final String path = httpConfig.serverURL + httpConfig.SERVER_GETTER + httpConfig.reqGroup + "/" + id;
        //+ httpConfig.GROUP_ID_PARAM + id + httpConfig.USER_ID_PARAM_2 + userID;

        startGetRequest(path, constantConfig.GET_RATING_TYPE, InvitationInteractor.this);
    }

    public void activationSubGroup(String id){
        final String path = httpConfig.serverURL + httpConfig.SERVER_GETTER + httpConfig.reqGroup + "/" + id;
        //+ httpConfig.GROUP_ID_PARAM + id + httpConfig.USER_ID_PARAM_2 + userID;

        startGetRequest(path, constantConfig.ACTIVATION_SUB_GROUP, InvitationInteractor.this);
    }

    public void getGroup(String id){
        final String path = httpConfig.serverURL + httpConfig.SERVER_GETTER + httpConfig.reqGroup + "/" + id;
        //+ httpConfig.GROUP_ID_PARAM + id + httpConfig.USER_ID_PARAM_2 + userID;

        startGetRequest(path, constantConfig.GET_GROUP_TYPE, InvitationInteractor.this);
    }

    private void getGroupForInvitation (String id){
        final String path = httpConfig.serverURL + httpConfig.SERVER_GETTER + httpConfig.reqGroup + "/" + id;
        //+ httpConfig.GROUP_ID_PARAM + id + httpConfig.USER_ID_PARAM_2 + userID;

        startGetRequest(path, constantConfig.GET_GROUP_FOR_INVITATION_TYPE, InvitationInteractor.this);
    }

    private void getUserForInvitation(final String id){

        final String path = httpConfig.serverURL + /*httpConfig.SERVER_GETTER*/"9003" + httpConfig.reqUser
                + "/" + id;

        startGetRequest(path, constantConfig.GET_USER_FOR_INVITATION_TYPE, InvitationInteractor.this);
    }

    public void discussionGroup(String id){
        final String path = httpConfig.serverURL + httpConfig.SERVER_GETTER + httpConfig.reqGroup + "/" + id;

        startGetRequest(path, constantConfig.EVENT_TYPE_DISCUSSION, InvitationInteractor.this);
    }


    @Override
    public void response(byte[] byteArray, String type) {
        if(type.equals(constantConfig.GET_INVITATION_TYPE)){
            prepareGetInvation(byteArray);
        }else if(type.equals(constantConfig.ACTIVATION_SUB_GROUP)){
            prepareGetGroupForComparison(byteArray);
        } else if (type.equals(constantConfig.GET_GROUP_TYPE)){
            prepareGetGroup(byteArray);
        } else if (type.equals(constantConfig.GET_GROUP_FOR_INVITATION_TYPE) || type.equals(constantConfig.GET_USER_FOR_INVITATION_TYPE)){
            prepareGetForInvitation(byteArray, type);
        } else if (type.equals(constantConfig.GET_RATING_TYPE)){
            prepareGetRating(byteArray);
        } else if (type.equals(constantConfig.EVENT_TYPE_DISCUSSION)){
            prepareGetDiscussion(byteArray);
        }
    }

    @Override
    public void error(Throwable t) {

    }

    @Override
    public void errorHanding(int responseCode, String type) {

    }

    private void prepareGetInvation (byte[] byteArray){
        invitation = createInvationOfBytes(byteArray);
        getGroupForInvitation(invitation.group);
        getUserForInvitation(invitation.initiator);

        InvitationDB invitationDB = invitation.initInvitationDB();
        invitationDataBase.invationDao().insert(invitationDB);
        delegate.answerInvitation();
    }

    private void prepareGetDiscussion(byte[] byteArray){
        Group group = new Group();
        group.nodeData = new NodeData();
        group.personal = new Personal();
        group = createGroupOfBytes(byteArray);
        delegate.answerDiscussionEvent(group);
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

    private void prepareGetRating(byte[] byteArray){
        Group group = new Group();
        group.nodeData = new NodeData();
        group.personal = new Personal();
        group = createGroupOfBytes(byteArray);
        delegate.answerRatingEvent(group);
    }

    private void prepareGetGroup (byte[] byteArray){
        try {
            Group group = new Group();
            group.nodeData = new NodeData();
            group.personal = new Personal();
            group = createGroupOfBytes(byteArray);
            if (group == null) {
//                delegate.error(new NullPointerException());
            }
            GroupDB groupDB = group.initGroupDB();

            noActiveGroupDataBase.noActiveGroupDao().insert(groupDB);



//            final Group finalGroup = group;
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        //отправка запроса на получение картинки
////                        getImageRequest(finalGroup);
//
//                        //отправка запроса на получение количества участников в группе
//                        // getMembersRequest(finalGroup);
//                    } catch (Exception error) {
//                        error(error);
//                    }
//                }
//            }).start();
//            Handler mainHandler = new Handler(Looper.getMainLooper());
//            Runnable myRunnable = new Runnable() {
//                @Override
//                public void run() {
////                    delegate.answerGetGroup(finalGroup);
//                }
//            };
//            mainHandler.post(myRunnable);
        } catch (Exception error) {
            error(error);
        }
    }

    private void prepareGetGroupForComparison (byte[] byteArray){

        Group group = new Group();
        group.nodeData = new NodeData();
        group.personal = new Personal();
        group = createGroupOfBytes(byteArray);
        GroupDB groupDB = groupDataBase.groupDao().getById(group.id);

        Group groupInDB = groupDB.initGroup();
        for (int i = 0; i < group.nodeData.children.size(); i++){
            for (int j = 0; j < groupInDB.nodeData.children.size(); i++){
                if (group.nodeData.children.get(i).equals(groupInDB.nodeData.children.get(j))){
                    group.nodeData.children.remove(i);
                }
            }
        }
        count = group.nodeData.children.size();
        for (int i = 0; i < group.nodeData.children.size(); i++){
            getGroup(group.nodeData.children.get(i));
        }
        if (group.admin.equals(QueryPreferences.getUserIdPreferences(context))) {
            groupDataBase.groupDao().update(group.initGroupDB());
            delegate.answerSubGroupCreatingRequest(group);
        }
    }

    private void prepareGetForInvitation(byte[] byteArray, String type){
        if (type.equals(constantConfig.GET_GROUP_FOR_INVITATION_TYPE)) {
            Group group = new Group();
            group.nodeData = new NodeData();
            group.personal = new Personal();
            group = createGroupOfBytes(byteArray);
            invitation.group = group.personal.descriptive.title;
            count = count+1;
        } else if (type.equals(constantConfig.GET_USER_FOR_INVITATION_TYPE)){
            User user = createUserOfBytes(byteArray);
            invitation.initiator = user.personal.descriptive.name + " " + user.personal.descriptive.surname;
            count = count+1;
        }
        if (count == 2){
            insertInvitation(invitation);
        }
    }

    private void insertInvitation(Invitation invitation){
        InvitationDB invitationDB = invitation.initInvitationDB();
        invitationDataBase.invationDao().insert(invitationDB);
    }





    //========================================================SUPPORT==========================//
    private Invitation createInvationOfBytes (byte[] byteArray){
        Gson gson = new Gson();
        String jsonString = new String(byteArray);
        return gson.fromJson(jsonString, Invitation.class);
    }

    private Group createGroupOfBytes(byte[] byteArray) {//---------------------------------------------создание User из массива байтов
        Gson gson = new Gson();
        String jsonString = new String(byteArray);
        return gson.fromJson(jsonString, Group.class);
    }

    private User createUserOfBytes(byte[] byteArray){
        Gson gson = new Gson();
        String jsonString = new String(byteArray);
        return gson.fromJson(jsonString, User.class);
    }

}

