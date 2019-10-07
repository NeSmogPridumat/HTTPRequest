package com.dreamteam.httprequest.database.NoActiveGroup;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.dreamteam.httprequest.database.Data.GroupDB;


@Database(entities = {GroupDB.class}, version = 1, exportSchema = false)
public abstract class NoActiveGroupDataBase extends RoomDatabase {
    public abstract NoActiveGroupDao noActiveGroupDao();
}
