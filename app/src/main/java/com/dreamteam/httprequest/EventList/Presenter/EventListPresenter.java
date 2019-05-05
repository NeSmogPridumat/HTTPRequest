package com.dreamteam.httprequest.EventList.Presenter;

import android.util.Log;

import com.dreamteam.httprequest.Event.Entity.Event;
import com.dreamteam.httprequest.EventList.EventListRouter;
import com.dreamteam.httprequest.EventList.Interactor.EventListInteractor;
import com.dreamteam.httprequest.EventList.Protocols.EventListPresenterInterface;
import com.dreamteam.httprequest.EventList.Protocols.EventListViewInterface;
import com.dreamteam.httprequest.MainActivity;

import java.util.ArrayList;

public class EventListPresenter implements EventListPresenterInterface {

    private EventListViewInterface delegate;
    private MainActivity activity;
    private EventListInteractor eventListInteractor = new EventListInteractor(this);
    private EventListRouter eventListRouter;

    public EventListPresenter (EventListViewInterface delegate, MainActivity activity){
        this.delegate = delegate;
        this.activity = activity;
        eventListRouter = new EventListRouter(activity);
    }

    public void getEvents (String userID){
        eventListInteractor.getEvents(userID);
    }

    @Override
    public void error(String error) {

    }

    @Override
    public void answerGetEvents(ArrayList<Event> eventArrayList) {
        Log.i("tam", "tadam");
        delegate.answerGetEvents(eventArrayList);
    }

    public void openEvent (Event event){
        eventListRouter.openEvent(event);
    }
}
