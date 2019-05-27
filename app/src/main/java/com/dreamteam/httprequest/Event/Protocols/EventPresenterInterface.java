package com.dreamteam.httprequest.Event.Protocols;

public interface EventPresenterInterface {
    void answerEvent();
    void answerServerToQuestion();
    void error(Throwable t);
}
