package com.dreamteam.httprequest.Event.Entity.Events;

import com.dreamteam.httprequest.Event.Entity.Events.EventsKinds.DataEvents.Event;
import com.dreamteam.httprequest.Event.Entity.Events.EventsKinds.Discussion;
import com.dreamteam.httprequest.Event.Entity.Events.EventsKinds.Poll;
import com.dreamteam.httprequest.Event.Entity.Events.EventsKinds.Rating;

import java.util.ArrayList;

public class Events {
    public ArrayList<Event> discussions = new ArrayList<>();
    public ArrayList<Event> polls = new ArrayList<>();
    public ArrayList<Event> ratings = new ArrayList<>();
}
