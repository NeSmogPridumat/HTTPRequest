package com.dreamteam.httprequest.User.Protocols;

import android.graphics.Bitmap;

import com.dreamteam.httprequest.Data.QuestionRating.QuestionRating;
import com.dreamteam.httprequest.Event.Entity.Events.EventsKinds.Rating;
import com.dreamteam.httprequest.Group.Entity.GroupData.Group;
import com.dreamteam.httprequest.Interfaces.PresenterInterface;
import com.dreamteam.httprequest.User.Entity.UserData.RatingData.RatingData;
import com.dreamteam.httprequest.User.Entity.UserData.User;
import java.util.ArrayList;

public interface PresenterUserInterface extends PresenterInterface {
    void answerGetUser(User user);

    void answerGetImage(Bitmap bitmap);

    void error (Throwable t);

    void openUser();

    void openUserAfterEdit();

    void answerGetGroupsForList(ArrayList<Group> groups);

    void answerGetRating(RatingData rating);
}
