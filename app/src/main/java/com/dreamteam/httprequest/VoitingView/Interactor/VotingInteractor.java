package com.dreamteam.httprequest.VoitingView.Interactor;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.dreamteam.httprequest.Data.ConstantConfig;
import com.dreamteam.httprequest.Data.HTTPConfig;
import com.dreamteam.httprequest.Event.Entity.AnswersVoting.AnswersVoting;
import com.dreamteam.httprequest.Event.Entity.InfoStartEvent.InfoStartEvent;
import com.dreamteam.httprequest.HTTPManager.HTTPManager;
import com.dreamteam.httprequest.Interfaces.OutputHTTPManagerInterface;
import com.dreamteam.httprequest.User.Entity.UserData.User;
import com.dreamteam.httprequest.User.Protocols.VotingPresenterInterface;
import com.dreamteam.httprequest.VoitingView.Data.Members;
import com.dreamteam.httprequest.VoitingView.Protocols.VotingHTTPManagerInterface;
import com.google.gson.Gson;

import java.io.IOException;

public class VotingInteractor implements VotingHTTPManagerInterface {
    HTTPManager httpManager = HTTPManager.get();
    HTTPConfig httpConfig = new HTTPConfig();
    ConstantConfig constantConfig = new ConstantConfig();
    private VotingPresenterInterface delegate;

    public VotingInteractor (VotingPresenterInterface delegate){
        this.delegate = delegate;
    }

    public void getUsersForVoting(String voitingId){
        String path = httpConfig.serverURL + httpConfig.SERVER_GETTER + httpConfig.EVENT + httpConfig.RATING + "/" + voitingId + httpConfig.UNANSWERED;
        startGetRequest(path, constantConfig.USERS_FOR_VOTING, VotingInteractor.this);
    }

    public void getUser(final String id) {//----------------------------------отправка запроса на получение User по id

        final String path = httpConfig.serverURL + /*httpConfig.SERVER_GETTER*/"9003" + httpConfig.reqUser
                + "/" + id;

        startGetRequest(path, constantConfig.USER_TYPE, VotingInteractor.this);
    }

    public void getImage(String userId){
        String path = httpConfig.serverURL + "9003" + httpConfig.USER + "/" + userId + httpConfig.IMAGE;
        startGetRequest(path, constantConfig.IMAGE_TYPE, VotingInteractor.this);
    }

    public void setVoitingAnswer(AnswersVoting answersVoting){
        String path = httpConfig.serverURL + httpConfig.SERVER_SETTER + httpConfig.RESPONSE + httpConfig.RATING;
        startPutRequest(path, answersVoting,constantConfig.VOITED, VotingInteractor.this);
    }

    @Override
    public void response(byte[] byteArray, String type) {
        if (type.equals(constantConfig.USERS_FOR_VOTING)){
            prepareGetUsersForVoting(byteArray);
        } else if (type.equals(constantConfig.USER_TYPE)){
            prepareGetUser(byteArray);
        } else if (type.equals(constantConfig.IMAGE_TYPE)){
            prepareGetImage(byteArray);
        } else if (type.equals(constantConfig.VOITED)){
            prepareAnswerVoting(byteArray);
        }
    }

    @Override
    public void error(Throwable t) {

    }

    @Override
    public void errorHanding(int responseCode, String type) {

    }

    public void prepareAnswerVoting(byte[] bytes){
        delegate.prepareAnswerVoting();
    }

    private void prepareGetUsersForVoting (byte[] byteArray){
        Members members = createMembersOfBytes(byteArray);
        if (members.members.size() != 0) {
            getUser(members.members.get(0));
        } else {
            Handler mainHandler = new Handler(Looper.getMainLooper());
            Runnable myRunnable = new Runnable() {
                @Override
                public void run() {
                    delegate.notUsers();
                }
            };
            mainHandler.post(myRunnable);
        }
    }

    private void prepareGetUser (byte[] byteArray){
        final User user = createUserOfBytes(byteArray);
        Handler mainHandler = new Handler(Looper.getMainLooper());
        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                delegate.answerGetUser(user);
            }
        };
        mainHandler.post(myRunnable);
    }

    private void prepareGetImage(byte[] byteArray) {//-------------------------------------------------------получение картинки(преобразование в bitmap)
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
            Thread.currentThread().interrupted();
        }
    }

    //============================================SUPPORT EVENT===============================//
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

    private void startPutRequest (final String path, final AnswersVoting answersVoting,
                                  final String type, final OutputHTTPManagerInterface delegate){
        new Thread(new Runnable() {//---------------------------------------------------------------запуск в фоновом потоке
            @Override
            public void run() {
                try {
                    Gson gson = new Gson();
                    final String jsonObject = gson.toJson(answersVoting);
                    httpManager.putRequest(path, jsonObject, type, delegate);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private Members createMembersOfBytes(byte[] byteArray){
        Gson gson = new Gson();
        String jsonString = new String(byteArray);
        return gson.fromJson(jsonString, Members.class);
    }

    private User createUserOfBytes(byte[] byteArray){
        Gson gson = new Gson();
        String jsonString = new String(byteArray);
        return gson.fromJson(jsonString, User.class);
    }
}
