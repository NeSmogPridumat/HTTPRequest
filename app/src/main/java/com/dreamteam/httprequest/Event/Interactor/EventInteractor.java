package com.dreamteam.httprequest.Event.Interactor;

import android.util.Log;

import com.dreamteam.httprequest.Event.Entity.Event;
import com.dreamteam.httprequest.Event.Protocols.EventFromHTTPManagerInterface;
import com.dreamteam.httprequest.Event.Protocols.EventPresenterInterface;
import com.dreamteam.httprequest.HTTPConfig;
import com.dreamteam.httprequest.HTTPManager;
import com.google.gson.Gson;

import java.io.IOException;

public class EventInteractor implements EventFromHTTPManagerInterface {

    private EventPresenterInterface delegate;
    private HTTPConfig httpConfig= new HTTPConfig();
    private HTTPManager httpManager = HTTPManager.get();


    public EventInteractor(EventPresenterInterface delegate){
        this.delegate = delegate;
    }

    @Override
    public void response(byte[] byteArray, String type) {
        if (type.equals("Answer for event")){
            Log.i("Вроде","Что-то ок");
        }
    }

    @Override
    public void error(Throwable t) {

    }

    @Override
    public void errorHanding(int resposeCode) {

    }

    public void answerEvent (final Event event){
        final String path = httpConfig.serverURL + httpConfig.SERVER_SETTER + httpConfig.EVENT + httpConfig.USER;
        new Thread(
                new Runnable() {//---------------------------------------------------------------запуск в фоновом потоке
                    @Override
                    public void run() {
                        Gson gson = new Gson();
                        String jsonObject = gson.toJson(event);
                        try {
                            httpManager.postRequest(path, jsonObject, "Answer for event",
                                    EventInteractor.this);//----------отправка в HTTPManager
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
    }

}
