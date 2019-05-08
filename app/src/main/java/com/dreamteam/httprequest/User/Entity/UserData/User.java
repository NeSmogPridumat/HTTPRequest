package com.dreamteam.httprequest.User.Entity.UserData;

import java.util.ArrayList;

public class User {
    public String id;
    public Content content = new Content();
    public ArrayList<QustionUser> questions;
    public int rules;
}
