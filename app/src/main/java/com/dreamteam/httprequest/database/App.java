package com.dreamteam.httprequest.database;

import android.app.Application;

import androidx.room.Room;

import com.dreamteam.httprequest.database.Group.GroupDataBase;
import com.dreamteam.httprequest.database.Invitation.InvitationDataBase;
import com.dreamteam.httprequest.database.NoActiveGroup.NoActiveGroupDataBase;
import com.dreamteam.httprequest.database.User.UserDataBase;

public class App extends Application {
    public static App instance;

    private UserDataBase userDataBase;
    private InvitationDataBase invitationDataBase;
    private GroupDataBase groupDataBase;
    private NoActiveGroupDataBase noActiveGroupDataBase;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        userDataBase = Room.databaseBuilder(this, UserDataBase.class, "userDB").build();
        invitationDataBase = Room.databaseBuilder(this, InvitationDataBase.class, "invitationDB").build();
        groupDataBase = Room.databaseBuilder(this, GroupDataBase.class, "groupDB").build();
        noActiveGroupDataBase = Room.databaseBuilder(this, NoActiveGroupDataBase.class, "noActiveGroupDB").build();
    }

    public static App getInstance(){
        return instance;
    }

    public UserDataBase getUserDataBase(){
        return userDataBase;
    }

    public InvitationDataBase getInvitationDataBase(){
        return invitationDataBase;
    }

    public GroupDataBase getGroupDataBase() {
        return groupDataBase;
    }

    public NoActiveGroupDataBase getNoActiveGroupDataBase(){
        return noActiveGroupDataBase;
    }
}
