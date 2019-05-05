package com.dreamteam.httprequest.Event.Presenter;

import com.dreamteam.httprequest.Event.Entity.Event;
import com.dreamteam.httprequest.Event.Interactor.EventInteractor;
import com.dreamteam.httprequest.Event.Protocols.EventPresenterInterface;
import com.dreamteam.httprequest.Event.Protocols.EventViewInterface;

public class EventPresenter implements EventPresenterInterface {

    private EventViewInterface delegate;
    private EventInteractor eventInteractor = new EventInteractor(this);

    public EventPresenter (EventViewInterface delegate){
        this.delegate = delegate;
    }

    public void answerEvent (Event event){
        eventInteractor.answerEvent(event);
    }
}
