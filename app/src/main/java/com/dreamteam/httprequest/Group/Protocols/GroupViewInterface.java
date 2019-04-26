package com.dreamteam.httprequest.Group.Protocols;

import android.graphics.Bitmap;

import com.dreamteam.httprequest.Group.Entity.GroupData.Group;

public interface GroupViewInterface {
    void outputImageView(Bitmap bitmap);

    void outputGroupView(Group group);

    void error(String error);

    void outputMembersView(int members);
}
