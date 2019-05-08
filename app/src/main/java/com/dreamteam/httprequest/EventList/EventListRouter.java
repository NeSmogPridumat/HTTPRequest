package com.dreamteam.httprequest.EventList;

import com.dreamteam.httprequest.Event.Entity.EventType12.Event;
import com.dreamteam.httprequest.Event.Entity.EventType4.EventType4;
import com.dreamteam.httprequest.MainActivity;

public class EventListRouter {

    private MainActivity activity;

    public EventListRouter(MainActivity activity){
        this.activity = activity;
    }

    public void openEvent(EventType4 event){
        activity.openEvent(event);
    }
}
