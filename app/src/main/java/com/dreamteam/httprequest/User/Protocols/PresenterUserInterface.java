package com.dreamteam.httprequest.User.Protocols;

import android.graphics.Bitmap;

import com.dreamteam.httprequest.Group.Entity.GroupData.Group;
import com.dreamteam.httprequest.Interfaces.PresenterInterface;
import com.dreamteam.httprequest.User.Entity.UserData.User;
import java.util.ArrayList;

public interface PresenterUserInterface extends PresenterInterface {
    void answerGetUser(User user);

    void answerGetImage(Bitmap bitmap);

    void error (String title, String description);

    void  answerGetGroups(int groups);

    void openUser();

    void openUserAfterEdit();

    void answerGetGroupsForList(ArrayList<Group> groups);
}
