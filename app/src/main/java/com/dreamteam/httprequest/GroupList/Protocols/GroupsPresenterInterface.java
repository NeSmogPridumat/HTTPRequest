package com.dreamteam.httprequest.GroupList.Protocols;

import android.graphics.Bitmap;

import com.dreamteam.httprequest.Group.Entity.GroupData.Group;
import com.dreamteam.httprequest.Interfaces.PresenterInterface;

import java.util.ArrayList;

public interface GroupsPresenterInterface extends PresenterInterface {

    void answerGetImageGroups(String groupID, Bitmap bitmap);

    void answerGetGroups(ArrayList<Group> groupCollection);

    void answerDeleteGroups();

    void error (Throwable t);

    void answerAddGroup(Group group);
}
