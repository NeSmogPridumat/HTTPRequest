package com.dreamteam.httprequest.Group.Protocols;

import android.graphics.Bitmap;

import com.dreamteam.httprequest.Event.Entity.EventType4.EventType4;
import com.dreamteam.httprequest.Group.Entity.GroupData.Group;
import com.dreamteam.httprequest.Interfaces.PresenterInterface;
import com.dreamteam.httprequest.ObjectList.ObjectData;
import com.dreamteam.httprequest.User.Entity.UserData.User;

import java.util.ArrayList;

public interface GroupPresenterInterface extends PresenterInterface {
    void error(String error);

    void answerGetGroup(Group group);

    void answerGetImage(Bitmap bitmap);

    void answerGetMembers(ArrayList<User> members, String type);

    void answerGetMembersForList (ArrayList<ObjectData> arrayList);

    void answerGetUsersForSelect (ArrayList<User> users, String type);

    void openGroupsList();

    void answerAddGroup(EventType4 event);

    void openGroupAfterSelect();

    void backPress();

    void answerStartVoited();

    void answerGetGroupAfterEdit (Group group);
}
