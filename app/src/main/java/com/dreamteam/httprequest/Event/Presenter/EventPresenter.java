package com.dreamteam.httprequest.Event.Presenter;

import com.dreamteam.httprequest.Event.Entity.AnswerQuestion.AnswerQuestion;
import com.dreamteam.httprequest.Event.EventRouter;
import com.dreamteam.httprequest.Event.Interactor.EventInteractor;
import com.dreamteam.httprequest.Event.Protocols.EventPresenterInterface;
import com.dreamteam.httprequest.Event.Protocols.EventViewInterface;
import com.dreamteam.httprequest.MainActivity;

public class EventPresenter implements EventPresenterInterface {

    private EventViewInterface delegate;
    private EventInteractor eventInteractor = new EventInteractor(this);
    private EventRouter eventRouter;

    public EventPresenter (EventViewInterface delegate, MainActivity activity){
        this.delegate = delegate;
        eventRouter = new EventRouter(activity);

    }

    public void answerEvent (AnswerQuestion eventResponse){
        eventInteractor.answerEvent(eventResponse);
    }

    @Override
    public void answerEvent() {
        eventRouter.backPress();
    }

    @Override
    public void answerServerToQuestion() {
        delegate.answerServerToQuestion();
    }

    public void resultToQuestion(AnswerQuestion answerQuestion){
        eventInteractor.resultToQuestion(answerQuestion);
    }

    public void openEventList(){
        eventRouter.openEventList();
    }
}
