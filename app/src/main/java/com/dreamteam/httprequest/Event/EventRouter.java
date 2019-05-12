package com.dreamteam.httprequest.Event;

import com.dreamteam.httprequest.MainActivity;
import com.dreamteam.httprequest.R;

public class EventRouter {

    private MainActivity activity;

    public EventRouter (MainActivity activity){
        this.activity = activity;
    }

    public void backPress(){
        activity.bottomNavigationView.setSelectedItemId(R.id.notification);//TODO
    }

    public void openEventList(){
        activity.openEventList();
    }
}
