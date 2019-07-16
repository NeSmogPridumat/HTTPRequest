package com.dreamteam.httprequest.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.dreamteam.httprequest.database.Data.UserDB;

@Database(entities = {UserDB.class}, version = 1)
public abstract class UserDataBase extends RoomDatabase {
    public abstract UserDao userDao();
}
