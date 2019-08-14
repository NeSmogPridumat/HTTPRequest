package com.dreamteam.httprequest.database.User;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.dreamteam.httprequest.database.Data.UserDB;

@Database(entities = {UserDB.class}, version = 1, exportSchema = false)
public abstract class UserDataBase extends RoomDatabase {
    public abstract UserDao userDao();
}
