package com.dreamteam.httprequest.User.Entity.UserData;

import com.dreamteam.httprequest.database.Data.UserDB;

import java.util.ArrayList;

public class User {
    public String id = null;
    public Content content = new Content();
    public ArrayList<QustionUser> questions;
    //public int rules;



    public UserDB initUserDB (){
        UserDB userDB = new UserDB();
        userDB.id = id;
        userDB.name = content.simpleData.name;
        userDB.surname = content.simpleData.surname;
        userDB.image = content.mediaData.image;
        return userDB;
    }
}
