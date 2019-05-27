package com.dreamteam.httprequest.Event.Interactor;

import android.content.res.Resources;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.dreamteam.httprequest.Data.ConstantConfig;
import com.dreamteam.httprequest.Event.Entity.AnswerQuestion.AnswerQuestion;
import com.dreamteam.httprequest.Event.Protocols.EventFromHTTPManagerInterface;
import com.dreamteam.httprequest.Event.Protocols.EventPresenterInterface;
import com.dreamteam.httprequest.Data.HTTPConfig;
import com.dreamteam.httprequest.HTTPManager.HTTPManager;
import com.dreamteam.httprequest.Interfaces.OutputHTTPManagerInterface;
import com.dreamteam.httprequest.R;
import com.google.gson.Gson;

import java.io.IOException;
import java.net.SocketTimeoutException;

import static android.support.constraint.Constraints.TAG;

public class EventInteractor implements EventFromHTTPManagerInterface {

    private EventPresenterInterface delegate;
    private HTTPConfig httpConfig= new HTTPConfig();
    private HTTPManager httpManager = HTTPManager.get();

    private ConstantConfig constantConfig = new ConstantConfig();

    public EventInteractor(EventPresenterInterface delegate){
        this.delegate = delegate;
    }

    //=========================================REQUESTS====================================//

    public void answerEvent (final AnswerQuestion eventResponse){
        final String path = httpConfig.serverURL + httpConfig.SERVER_SETTER + httpConfig.EVENT
                + httpConfig.USER;

        startPostRequest(path, eventResponse, constantConfig.ANSWER_FOR_EVENT_TYPE,
                EventInteractor.this);
    }

    public void resultToQuestion (final AnswerQuestion answerQuestion){
        final String path = httpConfig.serverURL + httpConfig.SERVER_SETTER + httpConfig.EVENT
                + httpConfig.USER;
        startPostRequest(path, answerQuestion, constantConfig.RESULT_TO_QUESTION_TYPE,
                EventInteractor.this);
    }

    //=========================ANSWERS=========================================================//
    @Override
    public void response(byte[] byteArray, String type) {
        if (type.equals(constantConfig.ANSWER_FOR_EVENT_TYPE)){
            eventAnswer();
        } else if (type.equals(constantConfig.RESULT_TO_QUESTION_TYPE)){
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
    public void error(final Throwable t) {

        Handler mainHandler = new Handler(Looper.getMainLooper());
        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                delegate.error(t);
            }
        };
        mainHandler.post(myRunnable);
        Log.e(TAG, "Failed server" + t.toString());
    }

    @Override
    public void errorHanding(int resposeCode, String type) {

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

    //=================================SUPPORT METHODS=============================//

    private void startPostRequest (final String path, final AnswerQuestion eventResponse,
                                   final String type, final OutputHTTPManagerInterface delegate){
        new Thread(new Runnable() {//---------------------------------------------------------------запуск в фоновом потоке
            @Override
            public void run() {
                try {
                    Gson gson = new Gson();
                    final String jsonObject = gson.toJson(eventResponse);
                    httpManager.postRequest(path, jsonObject, type, delegate);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}
