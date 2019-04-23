package com.dreamteam.httprequest.Group.Protocols;

import android.graphics.Bitmap;

import com.dreamteam.httprequest.Group.Entity.GroupData.Group;

public interface GroupViewInterface {
    void outputImageView(Bitmap bitmap);

    void outputGroupView(String title, String description);

    void error(String error);

    void outputMembersView(int members);
}
