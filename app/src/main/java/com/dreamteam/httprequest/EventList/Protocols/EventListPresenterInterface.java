package com.dreamteam.httprequest.EventList.Protocols;

import com.dreamteam.httprequest.Event.Entity.Event;

import java.util.ArrayList;

public interface EventListPresenterInterface {
    void error (String error);
    void answerGetEvents (ArrayList<Event> eventArrayList);
}
