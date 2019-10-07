package com.dreamteam.httprequest.VoitingView.Protocols;

import android.graphics.Bitmap;

import com.dreamteam.httprequest.User.Entity.UserData.User;

import java.util.ArrayList;

public interface SelectedPresenterInterface {

    void answergetUsers (ArrayList<User> users);

    void answerGetImageGroups(String groupID, Bitmap bitmap);
}
