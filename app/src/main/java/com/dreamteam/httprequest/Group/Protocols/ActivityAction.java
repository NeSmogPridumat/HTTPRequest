package com.dreamteam.httprequest.Group.Protocols;

import com.dreamteam.httprequest.Group.Entity.GroupData.Group;
import com.dreamteam.httprequest.Interfaces.PresenterInterface;

public interface ActivityAction {
    void getGroup(String id);
    void showFragment(PresenterInterface delegate, int i);
}
