package com.dreamteam.httprequest.EventList.Interactor;

import android.os.Handler;
import android.os.Looper;

import com.dreamteam.httprequest.Data.ConstantConfig;
import com.dreamteam.httprequest.Event.Entity.Event;
import com.dreamteam.httprequest.EventList.Protocols.EventListFromHTTPManagerInterface;
import com.dreamteam.httprequest.EventList.Protocols.EventListPresenterInterface;
import com.dreamteam.httprequest.HTTPConfig;
import com.dreamteam.httprequest.HTTPManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

public class EventListInteractor implements EventListFromHTTPManagerInterface {

    private HTTPConfig httpConfig = new HTTPConfig ();
    private HTTPManager httpManager = HTTPManager.get();
    private ConstantConfig constantConfig = new ConstantConfig();

    private EventListPresenterInterface delegate;

    public EventListInteractor (EventListPresenterInterface delegate){
        this.delegate = delegate;
    }

    public void getEvents(String userID){
        final String path = httpConfig.serverURL + httpConfig.SERVER_GETTER + httpConfig.EVENT + httpConfig.USER + httpConfig.USER_ID_PARAM + userID;
        //TODO путь для получения списка эвентов

        new Thread(new Runnable() {
            @Override
            public void run() {
                httpManager.getRequest(path, constantConfig.GET_EVENT_TYPE,  EventListInteractor.this);
            }
        }).start();
    }

    @Override
    public void response(byte[] byteArray, String type) {
        if (type.equals(constantConfig.GET_EVENT_TYPE)){
            prepareGetEventsResponse(byteArray);
        }
    }

    @Override
    public void error(Throwable t) {

    }

    private void prepareGetEventsResponse (byte[] byteArray){
        if (byteArray != null){
            final ArrayList<Event> eventArrayList = createEventsOfBytes(byteArray);//TODO занимает много времени на Samsung
            if (eventArrayList == null){
                String error = " ";
                delegate.error(error);
            } else {

                Handler mainHandler = new Handler(Looper.getMainLooper());
                Runnable myRunnable = new Runnable() {
                    @Override
                    public void run() {
                        delegate.answerGetEvents(eventArrayList);
                    }
                };
                mainHandler.post(myRunnable);

//                ArrayList<Event> grs = groupCollection;
//                getImageRequest(grs);
            }
        }
    }

    private ArrayList<Event> createEventsOfBytes (byte[] byteArray){
        Gson gson = new Gson();
        String jsonString = new String(byteArray);
        return gson.fromJson(jsonString, new TypeToken<ArrayList<Event>>(){}.getType());
    }

    @Override
    public void errorHanding(int responseCode) {

    }
}
