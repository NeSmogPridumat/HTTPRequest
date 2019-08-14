package com.dreamteam.httprequest.Event.Interactor;

import com.dreamteam.httprequest.Data.ConstantConfig;
import com.dreamteam.httprequest.Data.HTTPConfig;
import com.dreamteam.httprequest.Event.Entity.Events.EventsKinds.DataEvents.Event;
import com.dreamteam.httprequest.Event.Protocols.GetterEventsFromHTTPManagerInterface;
import com.dreamteam.httprequest.Group.Entity.GroupData.Group;
import com.dreamteam.httprequest.Group.Protocols.GroupPresenterInterface;
import com.dreamteam.httprequest.HTTPManager.HTTPManager;
import com.dreamteam.httprequest.Interfaces.OutputHTTPManagerInterface;
import com.dreamteam.httprequest.User.Entity.UserData.User;
import com.google.gson.Gson;

public class GetterEvents implements GetterEventsFromHTTPManagerInterface {
    private GroupPresenterInterface delegate;
    private Event event;
    private String type;
    private HTTPManager httpManager = HTTPManager.get();
    private ConstantConfig  constantConfig = new ConstantConfig();
    private HTTPConfig httpConfig = new HTTPConfig();
    private int count = 0;

    public GetterEvents(GroupPresenterInterface delegate) {
        this.delegate = delegate;
    }

    public void setEvent (Event event, String type){
        getGroup(event.group);
        getCreator(event.creator);
        this.event = event;
        this.type = type;
    }

    private void getGroup(String id) {
        final String path = httpConfig.serverURL + httpConfig.SERVER_GETTER + httpConfig.reqGroup + "/" + id;
        //+ httpConfig.GROUP_ID_PARAM + id + httpConfig.USER_ID_PARAM_2 + userID;
        startGetRequest(path, constantConfig.GET_GROUP_TYPE, GetterEvents.this);
    }

    private void getCreator(final String id) {//----------------------------------отправка запроса на получение User по id

        final String path = httpConfig.serverURL + httpConfig.SERVER_GETTER + httpConfig.reqUser
                + "/" + id;
        startGetRequest(path, constantConfig.USER_TYPE, GetterEvents.this);
    }

    @Override
    public void response(byte[] byteArray, String type) {
        if(type.equals(constantConfig.USER_TYPE)){
            prepareGetCreator(byteArray);
        } else if (type.equals(constantConfig.GET_GROUP_TYPE)){
            prepareGetGroup(byteArray);
        }
    }

    @Override
    public void error(Throwable t) {

    }

    @Override
    public void errorHanding(int responseCode, String type) {

    }

    private void prepareGetCreator(byte[] byteArray){
        User creator = createUserOfBytes(byteArray);
        event.creator = creator.personal.descriptive.name + " " + creator.personal.descriptive.surname;
        count++;
        if (count==2){
            delegate.answerGetterEvent(event, type);
        }
    }

    private void prepareGetGroup(byte[] byteArray){
        Group group = createGroupOfBytes(byteArray);
        event.group = group.personal.descriptive.title;
        count++;
        if (count==2){
            delegate.answerGetterEvent(event, type);
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

    private User createUserOfBytes(byte[] byteArray){
        Gson gson = new Gson();
        String jsonString = new String(byteArray);
        return gson.fromJson(jsonString, User.class);
    }

    private Group createGroupOfBytes(byte[] byteArray) {//---------------------------------------------создание User из массива байтов
        Gson gson = new Gson();
        String jsonString = new String(byteArray);
        return gson.fromJson(jsonString, Group.class);
    }
}
