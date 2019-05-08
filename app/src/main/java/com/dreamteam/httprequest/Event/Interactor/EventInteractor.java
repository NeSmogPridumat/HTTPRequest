package com.dreamteam.httprequest.Event.Interactor;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.dreamteam.httprequest.Event.Entity.AnswerQuestion.AnswerQuestion;
import com.dreamteam.httprequest.Event.Entity.EventAnswer.EventResponseType12;
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
            eventAnswer();
        } else if (type.equals("Result To Question")){
            answerServerToQuestion();
        }
    }

    private void eventAnswer(){
        Handler mainHandler = new Handler(Looper.getMainLooper());
        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                delegate.answerEvent();

            }
        };
        mainHandler.post(myRunnable);
    }

    @Override
    public void error(Throwable t) {

    }

    @Override
    public void errorHanding(int resposeCode) {

    }

    public void answerEvent (final AnswerQuestion eventResponse){
        final String path = httpConfig.serverURL + httpConfig.SERVER_SETTER + httpConfig.EVENT + httpConfig.USER;
        new Thread(
                new Runnable() {//---------------------------------------------------------------запуск в фоновом потоке
                    @Override
                    public void run() {
                        Gson gson = new Gson();
                        String jsonObject = gson.toJson(eventResponse);
                        try {
                            httpManager.postRequest(path, jsonObject, "Answer for event",
                                    EventInteractor.this);//----------отправка в HTTPManager
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
    }

    public void resultToQuestion (final AnswerQuestion answerQuestion){
        final String path = httpConfig.serverURL + httpConfig.SERVER_SETTER + httpConfig.EVENT + httpConfig.USER;
        new Thread(
                new Runnable() {//---------------------------------------------------------------запуск в фоновом потоке
                    @Override
                    public void run() {
                        Gson gson = new Gson();
                        String jsonObject = gson.toJson(answerQuestion);
                        try {
                            httpManager.postRequest(path, jsonObject, "Result To Question",
                                    EventInteractor.this);//----------отправка в HTTPManager
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
    }

    private void answerServerToQuestion(){
        Handler mainHandler = new Handler(Looper.getMainLooper());
        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                delegate.answerServerToQuestion();
            }
        };
        mainHandler.post(myRunnable);
    }

}
