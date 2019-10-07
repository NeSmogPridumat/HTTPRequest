package com.dreamteam.httprequest.EventList.Protocols;

import com.dreamteam.httprequest.Interfaces.OutputHTTPManagerInterface;
import com.dreamteam.httprequest.Invation.Entity.Invitation.Invitation;

public interface EventListFromHTTPManagerInterface extends OutputHTTPManagerInterface {
    void answerGetterInvitation(Invitation invitation);
}
