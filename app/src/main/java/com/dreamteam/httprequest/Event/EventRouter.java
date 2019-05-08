package com.dreamteam.httprequest.Event;

import com.dreamteam.httprequest.MainActivity;

public class EventRouter {

    private MainActivity activity;

    public EventRouter (MainActivity activity){
        this.activity = activity;
    }

    public void backPress(){
        activity.onBackPressed();
    }

    public void openEventList(){
        activity.openEventList();
    }
}
