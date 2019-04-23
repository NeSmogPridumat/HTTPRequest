package com.dreamteam.httprequest.GroupList.Protocols;

import android.graphics.Bitmap;

import com.dreamteam.httprequest.Group.Entity.GroupData.Group;

import java.util.ArrayList;

public interface GroupsViewInterface {

    void redrawAdapter(String groupID, Bitmap bitmap);

    void outputGroupsView (ArrayList<Group> groupCollection);
}
