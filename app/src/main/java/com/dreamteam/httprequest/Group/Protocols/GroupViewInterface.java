package com.dreamteam.httprequest.Group.Protocols;

import android.graphics.Bitmap;

import com.dreamteam.httprequest.Group.Entity.GroupData.Group;
import com.dreamteam.httprequest.User.Entity.UserData.User;

import java.util.ArrayList;

public interface GroupViewInterface {
    void outputImageView(Bitmap bitmap);

    void outputGroupView(Group group);

    void error(String title, String description);

    void outputMembersView(ArrayList<User> members);

    void answerStartVoited();
}
