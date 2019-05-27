package com.dreamteam.httprequest.Group.Protocols;

import android.graphics.Bitmap;

import com.dreamteam.httprequest.Group.Entity.GroupData.Group;
import com.dreamteam.httprequest.User.Entity.UserData.User;

import java.util.ArrayList;

public interface GroupViewInterface {
    void outputImageView(Bitmap bitmap);

    void outputGroupView(Group group);

    void error(Throwable t);

    void outputMembersView(ArrayList<User> members);

    void answerStartVoited();

    void errorHanding(String title, String descripton);
}
