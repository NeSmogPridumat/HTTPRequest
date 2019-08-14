package com.dreamteam.httprequest.EventList.Presenter;

import com.dreamteam.httprequest.Event.Entity.EventType4.EventType4;
import com.dreamteam.httprequest.EventList.EventListRouter;
import com.dreamteam.httprequest.EventList.Interactor.EventListInteractor;
import com.dreamteam.httprequest.EventList.Protocols.EventListPresenterInterface;
import com.dreamteam.httprequest.EventList.Protocols.EventListViewInterface;
import com.dreamteam.httprequest.Group.Entity.GroupData.Group;
import com.dreamteam.httprequest.Invation.Entity.Invitation.Invitation;
import com.dreamteam.httprequest.MainActivity;
import com.dreamteam.httprequest.QueryPreferences;
import com.dreamteam.httprequest.database.Data.InvitationDB;

import java.util.ArrayList;
import java.util.List;

public class EventListPresenter implements EventListPresenterInterface {

    private EventListViewInterface delegate;
    private EventListInteractor eventListInteractor;
    private EventListRouter eventListRouter;
    private MainActivity activity;

    public EventListPresenter (EventListViewInterface delegate, MainActivity activity){
        this.delegate = delegate;
        eventListRouter = new EventListRouter(activity);
        eventListInteractor = new EventListInteractor(this, QueryPreferences.getUserIdPreferences(activity));
    }

    public void getInvations(){
        eventListInteractor.getInvations();
    }

//    public void getEvents (String userID){
//        eventListInteractor.getEvents(userID);
//    }

    public void openEvent (EventType4 event){
        eventListRouter.openEvent(event);
    }

    @Override
    public void error(Throwable t) {
        delegate.error(t);
    }

    @Override
    public void answerGetEvents(ArrayList<EventType4> eventArrayList) {
        delegate.answerGetEvents(eventArrayList);
    }

    @Override
    public void answerGetInvitations(List<Invitation> invitationArrayList) {
        delegate.answerGetInvitations (invitationArrayList);
    }

    @Override
    public void answerGetSubGroupsNotActive(ArrayList<Group> subGroups) {
        delegate.answerGetSubGroupsNotActive(subGroups);
    }

    @Override
    public void answerInvited() {
        getInvations();
    }

    @Override
    public void openGroups() {
        eventListRouter.openGroupList();
    }

    public void setAnswerInvited(String invitationDBId, String answer){
        eventListInteractor.setAnswerInvited(invitationDBId, answer);
    }

    public void getSubGroupNotActive(){
        eventListInteractor.getSubGroupNotActive();
    }

    public void activatedSubGroup(String group, String answer){
        eventListInteractor.activatedSubGroup(group, answer);
    }

    public void getRatingEvent(){
        eventListInteractor.getEvents();
    }

    public void getEvents(){
        eventListInteractor.getEvents();
    }

    public void getNotification (String userId){
        eventListInteractor.getNotification(userId);
    }
}
