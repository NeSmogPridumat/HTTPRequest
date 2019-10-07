package com.dreamteam.httprequest.SelectList.Protocol;

import android.graphics.Bitmap;

import com.dreamteam.httprequest.User.Entity.UserData.User;

import java.util.ArrayList;

public interface SelectPresenter {

    void answerGetUsers(ArrayList<User> users);

    void answerGetImageGroups(String groupID, Bitmap finalBitmap);
}
