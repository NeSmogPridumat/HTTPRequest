package com.dreamteam.httprequest.EventList.Interactor;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.dreamteam.httprequest.Data.AnswerStatus;
import com.dreamteam.httprequest.Data.ConstantConfig;
import com.dreamteam.httprequest.Group.Entity.GroupData.Group;
import com.dreamteam.httprequest.Interfaces.OutputHTTPManagerInterface;
import com.dreamteam.httprequest.Invation.Entity.Invitation.Invitation;
import com.dreamteam.httprequest.Invation.Entity.Invitation.Invites;
import com.dreamteam.httprequest.EventList.Protocols.EventListFromHTTPManagerInterface;
import com.dreamteam.httprequest.EventList.Protocols.EventListPresenterInterface;
import com.dreamteam.httprequest.Data.HTTPConfig;
import com.dreamteam.httprequest.HTTPManager.HTTPManager;
import com.dreamteam.httprequest.database.App;
import com.dreamteam.httprequest.database.Data.GroupDB;
import com.dreamteam.httprequest.database.Data.InvitationDB;
import com.dreamteam.httprequest.database.Invitation.InvitationDataBase;
import com.dreamteam.httprequest.database.NoActiveGroup.NoActiveGroupDataBase;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EventListInteractor implements EventListFromHTTPManagerInterface {

    private HTTPConfig httpConfig = new HTTPConfig ();
    private HTTPManager httpManager = HTTPManager.get();
    private ConstantConfig constantConfig = new ConstantConfig();
    private String groupId;
    private String invitationDBId;
    private int countInvites;
    private String userid;
    private ArrayList<Invitation> invitations;
    private InvitationDataBase invitationDataBase = App.getInstance().getInvitationDataBase();
    private NoActiveGroupDataBase noActiveGroupDataBase = App.getInstance().getNoActiveGroupDataBase();

    private ArrayList<Group> subGroups = new ArrayList<>();

    private String invitationId;
    private EventListPresenterInterface delegate;

    public EventListInteractor (EventListPresenterInterface delegate, String id){
        this.delegate = delegate;
        this.userid = id;
    }

    //======================================REQUESTS====================================//

//    public void getEvents(String userID){
//        final String path = httpConfig.serverURL + httpConfig.SERVER_GETTER + httpConfig.INVITATION;
////                + httpConfig.USER + httpConfig.USER_ID_PARAM + userID;
//        //TODO путь для получения списка эвентов
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                httpManager.getRequest(path, constantConfig.GET_EVENT_TYPE,
//                        EventListInteractor.this);
//            }
//        }).start();
//    }

    public void getInvations(){
        countInvites = 0;
        invitations = new ArrayList<>();
        String path = httpConfig.serverURL + httpConfig.SERVER_GETTER + httpConfig.INVITATION + "?expanded=true";
        startGetRequest(path, constantConfig.GET_INVITATIONS_TYPE, this);
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                final List<InvitationDB> invitationDBArrayList = invitationDataBase.invationDao().getAll();
//                for(InvitationDB invitationDB: invitationDBArrayList){
//                    Log.i("INVITATION", invitationDB.id);
//                }
//
//                Handler mainHandler = new Handler(Looper.getMainLooper());
//                Runnable myRunnable = new Runnable() {
//                    @Override
//                    public void run() {
//                        delegate.answerGetInvitations (invitationDBArrayList);
//                    }
//                };
//                mainHandler.post(myRunnable);
//
//            }
//        }).start();

        }


    private void getInvitation(String idInvitation){

        final String path = httpConfig.serverURL + httpConfig.SERVER_GETTER + httpConfig.INVITATION + "/" + idInvitation;
//                + httpConfig.USER + httpConfig.USER_ID_PARAM + userID;
        //TODO путь для получения списка эвентов
        startGetRequest(path, constantConfig.GET_INVITATION_TYPE, this);
    }

    public void setAnswerInvited (String invitationDBId, String answer){
        this.invitationDBId = invitationDBId;
        AnswerStatus answerStatus = new AnswerStatus();
        answerStatus.status = answer;
        invitationId = invitationDBId;
        String path = httpConfig.serverURL + httpConfig.SERVER_SETTER + httpConfig.INVITATION + "/" + invitationDBId;
        startPostRequest(path, answerStatus, constantConfig.ANSWER_FOR_EVENT_TYPE, this);
    }

    public void getSubGroupNotActive(){

        new Thread(new Runnable() {//---------------------------------------------------------------запуск в фоновом потоке
            @Override
            public void run() {
                final List<GroupDB> subGroupNotActive = noActiveGroupDataBase.noActiveGroupDao().getAll();
                final ArrayList<Group> subGroupsNotActive = new ArrayList<>();
                for (int i = 0; i < subGroupNotActive.size(); i++) {
                    Group group = subGroupNotActive.get(i).initGroup();
                    subGroupsNotActive.add(group);
                }

                answerGetSubGroupsNotActive(subGroupsNotActive);

            }
        }).start();


//            count = subGroupNotActive.size();
//            for (int i = 0; i < subGroupNotActive.size(); i++) {
////                final String path = httpConfig.serverURL + httpConfig.SERVER_GETTER + httpConfig.reqGroup + "/" + subGroupNotActive.get(i);
////                //+ httpConfig.GROUP_ID_PARAM + id + httpConfig.USER_ID_PARAM_2 + userID;
////
////                startGetRequest(path, constantConfig.GET_GROUP_TYPE, EventListInteractor.this);
//            }
    }



    public void activatedSubGroup(final String groupId, String answer){
        this.groupId = groupId;
        final String path = httpConfig.serverURL + httpConfig.SERVER_SETTER + httpConfig.reqGroup + "/" + groupId + httpConfig.STATUS;


        AnswerStatus answerStatus = new AnswerStatus();
        answerStatus.status = answer;
        startPostRequest(path, answerStatus, constantConfig.ACTIVATED_GROUP_TYPE, this);
    }

    public void getEvents(){
        String path = httpConfig.serverURL + httpConfig.SERVER_GETTER + httpConfig.EVENT + "?status=active&expanded=true";
//        startGetRequest(path, constantConfig.EVENTS_TYPE, this);
    }

    public void getNotification(String userId){
        String path = httpConfig.serverURL + httpConfig.SERVER_GETTER + httpConfig.NOTIFICATION + "?after=" + userId;
        startGetRequest(path, constantConfig.EVENTS_TYPE, this);
    }

    //=======================================ANSWERS========================================//

    @Override
    public void response(byte[] byteArray, String type) {
        if (type.equals(constantConfig.GET_EVENT_TYPE)){
            prepareGetEventsResponse(byteArray);
        } else if (type.equals(constantConfig.GET_INVITATION_TYPE)) {
            prepareGetInvitationResponse(byteArray);
        } else if (type.equals(constantConfig.GET_GROUP_TYPE)){
            prepareGetSubGroupNotActive(byteArray);
        } else if (type.equals(constantConfig.ACTIVATED_GROUP_TYPE)){
            prepareActivatedSubGroup(byteArray);
        } else if (type.equals(constantConfig.ANSWER_FOR_EVENT_TYPE)){
            prepareAnswerInvited( byteArray);
        }else if (type.equals(constantConfig.GET_INVITATIONS_TYPE)){
            prepareGetInvitationsResponse(byteArray);
        } else if (type.equals(constantConfig.EVENTS_TYPE)){
            prepareGetEvents(byteArray);
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

    private void prepareAnswerInvited(byte[] byteArray){
        if (byteArray != null) {
            String answer = new String(byteArray);
            if (answer != null) {
//                invitationDataBase.invationDao().getById(invitationId);
//                invitationDataBase.invationDao().delete(invitationDataBase.invationDao().getById(invitationDBId));
                delegate.answerInvited();
            }
        }
    }

    private void prepareGetEvents(byte[] byteArray){
        String string = new String (byteArray);
        Log.i("EVENTS", string);
    }

    private void prepareGetEventsResponse (byte[] byteArray){
        if (byteArray != null){
//            final ArrayList<EventType4> eventArrayList;
            try {
                Invites invites = createEventsOfBytes(byteArray);
                if (invites == null){
                    delegate.error(new NullPointerException());
                } else {
//                    count
                    for( int i = 0; i < invites.invites.size(); i++){
                        getInvitation(invites.invites.get(i).id);
                    }
                    Handler mainHandler = new Handler(Looper.getMainLooper());
                    Runnable myRunnable = new Runnable() {
                        @Override
                        public void run() {
                            delegate.answerGetEvents(null);
                        }
                    };
                    mainHandler.post(myRunnable);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void prepareGetInvitationResponse (byte[] byteArray){
//        try {
//            Invitation invitation = createInvitationOfBytes(byteArray);
//            invitations.add(invitation);
//            if (invitations.size() == countInvites){
//                ArrayList<InvitationDB> invitationDB = new ArrayList<>();
//                for (Invitation invitation1: invitations){
//                    invitationDB.add(invitation1.initInvitationDB());
//                }
//                delegate.answerGetInvitations(invitationDB);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    private void prepareGetSubGroupNotActive(byte[] byteArray){
//        Group group = new Group();
//        group.nodeData = new NodeData();
//        group.personal = new Personal();
//        group = createGroupOfBytes(byteArray);
//        subGroups.add(group);
//        if (subGroups.size() == count){
//            Handler mainHandler = new Handler(Looper.getMainLooper());
//            Runnable myRunnable = new Runnable() {
//                @Override
//                public void run() {
//                    delegate.answerGetSubGroupsNotActive(subGroups);
//                }
//            };
//            mainHandler.post(myRunnable);
//
//        }
    }

    private void prepareActivatedSubGroup(byte[] byteArray){
        if (byteArray != null) {
            String jsonString = new String(byteArray);
            GroupDB groupDB = noActiveGroupDataBase.noActiveGroupDao().getById(groupId);
            noActiveGroupDataBase.noActiveGroupDao().delete(groupDB);
        }
        getSubGroupNotActive();
    }


    @Override
    public void errorHanding(int responseCode, String type) {
        Log.i("dflshjdf","sdfjsdjf");
    }

    private void answerGetSubGroupsNotActive(final ArrayList<Group> subGroupsNotActive) {
        if (subGroupsNotActive != null) {
            Handler mainHandler = new Handler(Looper.getMainLooper());
            Runnable myRunnable = new Runnable() {
                @Override
                public void run() {
                    delegate.answerGetSubGroupsNotActive(subGroupsNotActive);
                }
            };
            mainHandler.post(myRunnable);
        }
    }

    private void prepareGetInvitationsResponse(byte[] byteArray){
        try {
            Invites invites = createInvitesOfBytes(byteArray);
            if(invites.invites != null) {
                countInvites = invites.invites.size();
            }
            if (countInvites != 0) {
                for (int i = 0; i < invites.invites.size(); i++) {
                    GetterInvitation getterInvitation = new GetterInvitation(EventListInteractor.this, userid);
                    getterInvitation.getInvitation(invites.invites.get(i));
                }
            } else {
                delegate.openGroups();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void answerGetterInvitation(Invitation invitation) {
        invitations.add(invitation);
        if(invitations.size() == countInvites){
            ArrayList<Invitation> delete = new ArrayList<>();
            for(int i = 0; i < invitations.size(); i++){
                if(invitations.get(i).group.equals("null")){
                    delete.add(invitations.get(i));
                }
            }
            for(int i = 0; i < delete.size(); i++){
                invitations.remove(delete.get(i));
            }
            delegate.answerGetInvitations(invitations);
        }
    }

    //====================================SUPPORT METHODS===================================//

    private Invites createEventsOfBytes (byte[] byteArray)throws Exception{

        Gson gson = new Gson();
        String jsonString = new String(byteArray);
//        JSONArray jsonArray = new JSONArray(jsonString);
        Invites invites = gson.fromJson(jsonString, new TypeToken<Invites>() {}.getType());

//        ArrayList<String> list = new ArrayList<>();
//        for (int i=0; i<jsonArray.length(); i++) {
//            list.add(jsonArray.getString(i));
//        }
//
//        ArrayList<EventType4> events = new ArrayList<>();
//        for (int i = 0; i < jsonArray.length(); i++){
//            if(!(jsonArray.get(i).equals("null"))) {
//                EventType4 event = gson.fromJson((list.get(i)), new TypeToken<EventType4>() {}.getType());
//                events.add(event);
//            }
//        }
        return invites;
    }

    private Invitation createInvitationOfBytes (byte[] byteArray) {

        Gson gson = new Gson();
        String jsonString = new String(byteArray);
        Invitation invitation = gson.fromJson(jsonString, new TypeToken<Invitation>() {}.getType());

        return invitation;
    }

    private Invites createInvitesOfBytes (byte[] byteArray) {

        Gson gson = new Gson();
        String jsonString = new String(byteArray);
        Invites invites = gson.fromJson(jsonString, new TypeToken<Invites>() {}.getType());

        return invites;
    }

    private void startPostRequest (final String path, final AnswerStatus answer,
                                   final String type, final OutputHTTPManagerInterface delegate){
        new Thread(new Runnable() {//---------------------------------------------------------------запуск в фоновом потоке
            @Override
            public void run() {
                try {
                    Gson gson = new Gson();
                    final String jsonObject = gson.toJson(answer);
                    httpManager.postRequest(path, jsonObject, type, delegate);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
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

    private Group createGroupOfBytes(byte[] byteArray) {//---------------------------------------------создание User из массива байтов
        Gson gson = new Gson();
        String jsonString = new String(byteArray);
        return gson.fromJson(jsonString, Group.class);
    }
}
