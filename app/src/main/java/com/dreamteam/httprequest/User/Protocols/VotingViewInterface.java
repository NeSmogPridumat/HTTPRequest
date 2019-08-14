package com.dreamteam.httprequest.User.Protocols;

import android.graphics.Bitmap;

import com.dreamteam.httprequest.User.Entity.UserData.User;

public interface VotingViewInterface {
    void answerGetUser(User user);

    void answerGetImage(Bitmap bitmap);

    void notUsers();
}
