package com.dreamteam.httprequest.Service;

import com.dreamteam.httprequest.Group.Entity.GroupData.Group;
import com.dreamteam.httprequest.Interfaces.OutputHTTPManagerInterface;

public interface ForServiceInterface  {

    void answerSubGroupCreatingRequest(Group group);

    void answerInvitation();

    void answerRatingEvent(Group group);

    void answerDiscussionEvent(Group group);
}
