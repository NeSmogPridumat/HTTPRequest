package com.dreamteam.httprequest.database.Data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import com.dreamteam.httprequest.User.Entity.UserData.User;

@Entity
public class UserDB {
    @PrimaryKey
    @NonNull
    public String id;
    public String name = null;
    public String surname = null;
    public String image = null;
    //public Bitmap bitmap;


    public User initUser(){
        User user = new User();
        user.id = id;
        user.content.simpleData.name = name;
        user.content.simpleData.surname = surname;
        user.content.mediaData.image = image;
        return user;
    }
}
