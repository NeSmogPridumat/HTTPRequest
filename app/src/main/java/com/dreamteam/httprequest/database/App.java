package com.dreamteam.httprequest.database;

import android.app.Application;
import android.arch.persistence.room.Room;

public class App extends Application {
    public static App instance;

    private UserDataBase userDataBase;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        userDataBase = Room.databaseBuilder(this, UserDataBase.class, "database").build();
    }

    public static App getInstance(){
        return instance;
    }

    public UserDataBase getUserDataBase(){
        return userDataBase;
    }
}
