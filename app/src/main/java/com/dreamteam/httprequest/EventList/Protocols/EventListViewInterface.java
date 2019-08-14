package com.dreamteam.httprequest.EventList.Protocols;

import com.dreamteam.httprequest.Event.Entity.EventType4.EventType4;
import com.dreamteam.httprequest.Group.Entity.GroupData.Group;
import com.dreamteam.httprequest.Invation.Entity.Invitation.Invitation;
import com.dreamteam.httprequest.database.Data.InvitationDB;

import java.util.ArrayList;
import java.util.List;

public interface EventListViewInterface {

    void answerGetEvents (ArrayList<EventType4> eventArrayList);

    void error (Throwable t);

    void answerGetInvitations (List<Invitation> invitationArrayList);

    void answerGetSubGroupsNotActive(ArrayList<Group> subGroups);
}
