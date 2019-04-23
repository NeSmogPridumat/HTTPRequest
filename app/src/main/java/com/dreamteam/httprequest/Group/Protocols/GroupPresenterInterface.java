package com.dreamteam.httprequest.Group.Protocols;

import android.graphics.Bitmap;

import com.dreamteam.httprequest.Group.Entity.GroupData.Group;

public interface GroupPresenterInterface {
    void error(String error);

    void answerGetGroup(String title, String description);

    void answerGetImage(Bitmap bitmap);

    void answerGetMembers(int members);
}
