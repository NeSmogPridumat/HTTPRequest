package com.dreamteam.httprequest.database.Invitation;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.dreamteam.httprequest.database.Data.InvitationDB;

@Database(entities = {InvitationDB.class}, version = 1, exportSchema = false)
public abstract class InvitationDataBase extends RoomDatabase {
    public abstract InvitationDao invationDao();
}
