package com.dreamteam.httprequest.User.Interactor;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.util.Log;

import com.dreamteam.httprequest.Data.AddData;
import com.dreamteam.httprequest.Data.ConstantConfig;
import com.dreamteam.httprequest.Data.QuestionRating.QuestionRating;
import com.dreamteam.httprequest.Data.RequestInfo;
import com.dreamteam.httprequest.Event.Entity.Events.EventsKinds.Rating;
import com.dreamteam.httprequest.Group.Entity.GroupData.Group;
import com.dreamteam.httprequest.Group.Entity.GroupData.GroupMediaData;
import com.dreamteam.httprequest.Data.HTTPConfig;
import com.dreamteam.httprequest.HTTPManager.HTTPManager;
import com.dreamteam.httprequest.Interfaces.OutputHTTPManagerInterface;
import com.dreamteam.httprequest.Interfaces.UserFromHTTPManagerInterface;
import com.dreamteam.httprequest.MultipartUtility;
import com.dreamteam.httprequest.User.Entity.UserData.RatingData.RatingData;
import com.dreamteam.httprequest.User.Protocols.PresenterUserInterface;
import com.dreamteam.httprequest.User.Entity.UserData.User;
import com.dreamteam.httprequest.database.App;
import com.dreamteam.httprequest.database.Data.UserDB;
import com.dreamteam.httprequest.database.User.UserDao;
import com.dreamteam.httprequest.database.User.UserDataBase;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class UserInteractor implements UserFromHTTPManagerInterface {

    private ConstantConfig constantConfig = new ConstantConfig();

    private HTTPManager httpManager = HTTPManager.get();

    private HTTPConfig httpConfig = new HTTPConfig();

    UserDataBase dataBase = App.getInstance().getUserDataBase();
    UserDao userDao = dataBase.userDao();

    private PresenterUserInterface delegate;

    public UserInteractor(PresenterUserInterface delegate) {
        this.delegate = delegate;
    }

//----------------------------------ОТПРАВКА В HTTPMANAGER---------------------------------------//

    public void getUser(final String id) {//----------------------------------отправка запроса на получение User по id

        //смотрим в БД, если там нет, делаем запрос
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //UserDB userDB = userDao.getById(id);
                    //if(userDB == null) {
                        final String path = httpConfig.serverURL + /*httpConfig.SERVER_GETTER*/"9003" + httpConfig.reqUser
                                + "/" + id;

                        startGetRequest(path, constantConfig.USER_TYPE, UserInteractor.this);
//                    } else {
//                        final User user = new User();
//                        user.id = userDB.id;
//                        user.content.simpleData.name = userDB.name;
//                        user.content.simpleData.surname = userDB.surname;
//                        user.content.mediaData.image = userDB.image;
//
//                        Handler mainHandler = new Handler(Looper.getMainLooper());
//                        Runnable myRunnable = new Runnable() {
//                            @Override
//                            public void run() {
//                                delegate.answerGetUser(user);
//                            }
//                        };
//                        mainHandler.post(myRunnable);
//
//                    }
                } catch (Exception error) {
                    error(error);
                }
            }
        }).start();
    }

    public void postUser(String name, String surname) {//--------------отправка post-запроса на сервер
        final User user = createUser(name, surname);
        Gson gson = new Gson();
        final String jsonObject = gson.toJson(user);
        final String path = httpConfig.serverURL + httpConfig.SERVER_SETTER + httpConfig.reqUser;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    httpManager.postRequest(path, jsonObject, constantConfig.POST_USER,
                            UserInteractor.this);
                } catch (Exception error) {
                    error(error);
                }
            }
        }).start();
    }

    public void postEditUser(final User user, final File bitmap){
        Gson gson = new Gson();
        final String jsonObject = gson.toJson(user.personal);
        final String path = httpConfig.serverURL + "9003" + httpConfig.USER + "/" + user.id;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    httpManager.postRequest(path, jsonObject, constantConfig.POST_USER,
                            UserInteractor.this);
                } catch (Exception error) {
                    error(error);
                }
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
            try {
                MultipartUtility multipartUtility = new MultipartUtility(httpConfig.serverURL + "9003" + httpConfig.USER + "/" + user.id + httpConfig.IMAGE, "UTF-8");
                multipartUtility.addFilePart("file", bitmap);
                List<String> response = multipartUtility.finish();
                Log.e("TAGGGG", "SERVER REPLIED:");
                for (String line : response) {
                    Log.e("TAAAAAGGGG", "Upload Files Response:::" + line);
                }

            } catch (Exception error) {
                error(error);
            }
            }
        }).start();

    }

    private void getAfterPostUser(byte[] byteArray) {
        //final User user = createUserOfBytes(byteArray);
        //String path = httpConfig.serverURL + httpConfig.SERVER_GETTER + httpConfig.reqUser + httpConfig.ID_PARAM + user.id;
        //startGetRequest(path, constantConfig.USER_TYPE, UserInteractor.this);

        final String status = createAnswerOfBytes(byteArray);
        if (status != null){
            delegate.openUserAfterEdit();
        }
    }

    public void getGroupForList (String userID){
        final String path = httpConfig.serverURL + httpConfig.SERVER_GETTER + httpConfig.reqGroup +
                httpConfig.reqUser + httpConfig.USER_ID_PARAM + userID;

        startGetRequest(path, constantConfig.GET_GROUP_FOR_LIST_TYPE, UserInteractor.this);
    }

    private void getImageResponse(User user) {//------------------------------------------------------получение картинки
        //String imageUrl = httpConfig.serverURL + httpConfig.SERVER_GETTER + user.content.mediaData.image;
        //startGetRequest(imageUrl, constantConfig.IMAGE_TYPE, UserInteractor.this);
    }

    //запрос на изменение объекта
    public void putUser(final User user, final Bitmap bitmap){
        final RequestInfo requestInfo = getRequestInfo(user);
        final String urlPath = httpConfig.serverURL + httpConfig.SERVER_SETTER + httpConfig.reqUser;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    //База данных, которая могёт!!!
                    //UserDB userDB = user.initUserDB();
                    //userDao.update(userDB);


                    Gson gson = new Gson();
                    if(bitmap != null){
                        requestInfo.addData.content.mediaData.image = decodeBitmapInBase64(bitmap);
                    }
                    final String jsonObject = gson.toJson(requestInfo);
                    httpManager.putRequest(urlPath, jsonObject, constantConfig.PUT_USER,
                            UserInteractor.this);
                } catch (Exception error) {
                    error(error);
                }
            }
        }).start();
    }

    public void getRating(String userID){
        String path = httpConfig.serverURL + httpConfig.SERVER_GETTER
                + httpConfig.USER + "/" + userID + httpConfig.RATING;

        startGetRequest(path, constantConfig.GET_RATING_TYPE, UserInteractor.this);
    }

    public void getImageThis(String userId){
        String path = httpConfig.serverURL + "9003" + httpConfig.USER + "/" + userId + httpConfig.IMAGE;
        startGetRequest(path, constantConfig.IMAGE_TYPE, UserInteractor.this);
    }

//----------------------------------------ПОЛУЧЕНИЕ И ОБРАБОТКА ДАННЫХ-----------------------------//

    @Override
    public void response(byte[] byteArray, String type) {//----------------------------------------получение ответа от HTTPManager и распределение по типу
        if (type.equals(constantConfig.USER_TYPE)) {
            getUserResponse(byteArray);
        } else if (type.equals(constantConfig.POST_USER)) {
            //TODO:возможно вынести в отдельный метод (либо совместить с getUser?)
            getAfterPostUser(byteArray);
        } else if (type.equals(constantConfig.IMAGE_TYPE)) {
            getImage(byteArray);
        } else if (type.equals(constantConfig.GET_GROUP_TYPE)) {
            //здесь было получение групп юзера
        }else if (type.equals(constantConfig.PUT_USER)){
            getUserEditResponse();
        } else if (type.equals(constantConfig.GET_GROUP_FOR_LIST_TYPE)){
            prepareGetGroupsForListResponse(byteArray);
        } else if (type.equals(constantConfig.GET_RATING_TYPE)){
            getRatingResponse(byteArray);
        }
    }

    private void getRatingResponse(byte[] byteArray){
        String string = new String(byteArray);
        Log.i("TAG", string);
        final RatingData rating = createRatingOfBytes(byteArray);
        Handler mainHandler = new Handler(Looper.getMainLooper());
        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                 delegate.answerGetRating(rating);
            }
        };
        mainHandler.post(myRunnable);

    }

    //открытие окна профиля после получения ответа
    private void getUserEditResponse(){
        delegate.openUserAfterEdit ();
    }

    @Override
    public void error(final Throwable t) {//--------------------------------------------------------------Обработка ошибки

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
    public void errorHanding(int responseCode, String type) {
        Log.i("Ошибочка"+ responseCode,"Тадам" );
    }

    //получение json ответа, преобразование его в User и вывод в основной поток
    private void getUserResponse(byte[] byteArray) {
        try {
            final User user = createUserOfBytes(byteArray);

            UserDB userDB = new UserDB();
            userDB.id = user.id;
            //userDB.name = user.content.simpleData.name;
            //userDB.surname = user.content.simpleData.surname;
            //userDB.image = user.content.mediaData.image;

            //userDao.insert(userDB);

            Handler mainHandler = new Handler(Looper.getMainLooper());
            Runnable myRunnable = new Runnable() {
                @Override
                public void run() {
                    delegate.answerGetUser(user);
                }
            };
            mainHandler.post(myRunnable);

            //getImageResponse(user);
            Thread.currentThread().interrupted();
        } catch (Exception error) {
            error(error);
        }
    }

    private void prepareGetGroupsForListResponse (byte[] byteArray){
        final ArrayList<Group> groupCollection = createGroupsOfBytes(byteArray);

        if (groupCollection != null){
            Handler mainHandler = new Handler(Looper.getMainLooper());
            Runnable myRunnable = new Runnable() {
                @Override
                public void run() {
                    delegate.answerGetGroupsForList(groupCollection);
                }
            };
            mainHandler.post(myRunnable);
        }
    }

    private void getImage(byte[] byteArray) {//-------------------------------------------------------получение картинки(преобразование в bitmap)
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

    //===============================SUPPORT METHODS===============================================//

    private RequestInfo getRequestInfo (User user){
        RequestInfo requestInfo = new RequestInfo();
        requestInfo.creatorID = user.id;
        requestInfo.addData = new AddData();
        requestInfo.addData.id = user.id;
        requestInfo.addData.content.mediaData = new GroupMediaData();
        requestInfo.addData.content.simpleData.name = user.personal.descriptive.name;
        requestInfo.addData.content.simpleData.surname = user.personal.descriptive.surname;
        return requestInfo;
    }

    //декодирование Bitmap в Base64
    private String decodeBitmapInBase64 (Bitmap bitmap){//------------------------------------------декодирование Bitmap в Base64
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        // Получаем изображение из потока в виде байтов
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return constantConfig.PREFIX + Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    private ArrayList<Group> createGroupsOfBytes (byte[] byteArray){//----------------------создание массива групп из массива байтов
        ArrayList<Group> groups = null;
        if (byteArray != null) {
            Gson gson = new Gson();
            String jsonString = new String(byteArray);
            groups = gson.fromJson(jsonString, new TypeToken<ArrayList<Group>>() {}.getType());
        }
        return groups;
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

    private User createUserOfBytes(byte[] byteArray){
        Gson gson = new Gson();
        String jsonString = new String(byteArray);
        return gson.fromJson(jsonString, User.class);
    }

    private RatingData createRatingOfBytes(byte[] byteArray){
        Gson gson = new Gson();
        String jsonString = new String(byteArray);
        return gson.fromJson(jsonString, RatingData.class);
    }

    private User createUser(String name, String surname) {
        User user = new User();
        user.personal.descriptive.name = name;
        user.personal.descriptive.surname = surname;
        return user;
    }

    private ArrayList<QuestionRating> createQuestionRatingOfBytes(byte[] byteArray){
        Gson gson = new Gson();
        String jsonString = new String(byteArray);
        return gson.fromJson(jsonString, new TypeToken<ArrayList<QuestionRating>>() {}.getType());
    }

    private String createAnswerOfBytes(byte[] byteArray){
        Gson gson = new Gson();
        String jsonString = new String(byteArray);
        //String status =  gson.fromJson(jsonString, String.class);
//        boolean responseBoolean = false;
//        if (answerAuth.status.equals("ok")){
//            responseBoolean = true;
//        }
        return jsonString;
    }

}


