package com.dreamteam.httprequest.SelectList.Protocol;

import android.graphics.Bitmap;

import com.dreamteam.httprequest.User.Entity.UserData.User;

import java.util.ArrayList;

public interface SelectView {

    void answerGetUsers(ArrayList<User> users);

    void redrawAdapter(String objectID, Bitmap bitmap);
}
