package com.dreamteam.httprequest.EventList;

import com.dreamteam.httprequest.Event.Entity.Event;
import com.dreamteam.httprequest.MainActivity;

public class EventListRouter {

    private MainActivity activity;

    public EventListRouter(MainActivity activity){
        this.activity = activity;
    }

    public void openEvent(Event event){
        activity.openEvent(event);
    }
}
