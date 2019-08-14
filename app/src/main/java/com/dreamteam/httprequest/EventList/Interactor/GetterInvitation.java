package com.dreamteam.httprequest.EventList.Interactor;

import android.os.Handler;
import android.os.Looper;

import com.dreamteam.httprequest.Data.ConstantConfig;
import com.dreamteam.httprequest.Data.HTTPConfig;
import com.dreamteam.httprequest.Event.Protocols.EventPresenterInterface;
import com.dreamteam.httprequest.EventList.Protocols.EventListFromHTTPManagerInterface;
import com.dreamteam.httprequest.EventList.Protocols.EventListPresenterInterface;
import com.dreamteam.httprequest.Group.Entity.GroupData.Group;
import com.dreamteam.httprequest.HTTPManager.HTTPManager;
import com.dreamteam.httprequest.Interfaces.OutputHTTPManagerInterface;
import com.dreamteam.httprequest.Invation.Entity.Invitation.Invitation;
import com.dreamteam.httprequest.QueryPreferences;
import com.dreamteam.httprequest.User.Entity.UserData.User;
import com.google.gson.Gson;

public class GetterInvitation implements EventListFromHTTPManagerInterface {

    final String GET_INITIATOR = "Get initiator";
    final String GET_RECEIVER = "Get receiver";

    private Invitation invitation;
    private EventListFromHTTPManagerInterface delegate;
    private HTTPConfig httpConfig = new HTTPConfig();
    private HTTPManager httpManager = HTTPManager.get();
    private ConstantConfig constantConfig = new ConstantConfig();
    private int count = 0;
    private String userId;

    public GetterInvitation(EventListFromHTTPManagerInterface delegate, String userId){
        this.userId = userId;
        this.delegate = delegate;
    }

    public void getInvitation (Invitation invitation){
        this.invitation = invitation;
        getInitiator(invitation.initiator);
        getReceiver(invitation.receiver);
        getGroup(invitation.group);
    }

    private void getInitiator(String id){
        final String path = httpConfig.serverURL + httpConfig.SERVER_GETTER + httpConfig.reqUser + "/" + id;

        startGetRequest(path, GET_INITIATOR, GetterInvitation.this);
    }

    private void getReceiver(String id){
        final String path = httpConfig.serverURL + httpConfig.SERVER_GETTER + httpConfig.reqUser + "/" + id;

        startGetRequest(path, GET_RECEIVER, GetterInvitation.this);
    }

    private void getGroup(String id){
            final String path = httpConfig.serverURL + httpConfig.SERVER_GETTER + httpConfig.reqGroup + "/" + id;
            //+ httpConfig.GROUP_ID_PARAM + id + httpConfig.USER_ID_PARAM_2 + userID;

            startGetRequest(path, constantConfig.GET_GROUP_TYPE, GetterInvitation.this);

    }

    @Override
    public void response(byte[] byteArray, String type) {
        if (type.equals(constantConfig.GET_GROUP_TYPE)){
            prepareGetGroup(byteArray);
        } else if (type.equals(GET_INITIATOR)){
            prepareGetInitiator(byteArray);
        } else if (type.equals(GET_RECEIVER)){
            prepareGetReceiver(byteArray);
        }
    }

    @Override
    public void error(Throwable t) {

    }

    @Override
    public void errorHanding(int responseCode, String type) {

    }

    private void prepareGetGroup(byte[] byteArray){
        Group group = createGroupOfBytes(byteArray);
        invitation.group = group.personal.descriptive.title;
        if (group.admin.equals(userId)){
            invitation.group = "null";
        }
        count++;
        answerGetterInvitation(invitation);
    }

    private void prepareGetInitiator(byte[] byteArray){
        User user = createUserOfBytes(byteArray);
        invitation.initiator = user.personal.descriptive.name;
        count++;
        answerGetterInvitation(invitation);
    }

    private void prepareGetReceiver (byte[] byteArray){
        User user = createUserOfBytes(byteArray);
        invitation.receiver = user.personal.descriptive.name + " " + user.personal.descriptive.surname;
        count++;
        answerGetterInvitation(invitation);
    }

    public void answerGetterInvitation(final Invitation invitation){
        if (count == 3){
            Handler mainHandler = new Handler(Looper.getMainLooper());
            Runnable myRunnable = new Runnable() {
                @Override
                public void run() {
                    delegate.answerGetterInvitation(invitation);
                }
            };
            mainHandler.post(myRunnable);

        }
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

    private User createUserOfBytes(byte[] byteArray){
        Gson gson = new Gson();
        String jsonString = new String(byteArray);
        return gson.fromJson(jsonString, User.class);
    }
}
