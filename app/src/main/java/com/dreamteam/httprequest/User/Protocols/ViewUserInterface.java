package com.dreamteam.httprequest.User.Protocols;

import android.graphics.Bitmap;

import com.dreamteam.httprequest.Data.QuestionRating.QuestionRating;
import com.dreamteam.httprequest.Event.Entity.Events.EventsKinds.Rating;
import com.dreamteam.httprequest.User.Entity.UserData.RatingData.RatingData;
import com.dreamteam.httprequest.User.Entity.UserData.User;

import java.util.ArrayList;

public interface ViewUserInterface {
    void View(User user);

    void ViewImage(Bitmap bitmap);

    void  error (Throwable t);

    void answerGetRating(RatingData rating);
}
