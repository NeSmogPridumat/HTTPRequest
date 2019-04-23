package com.dreamteam.httprequest.GroupList.Protocols;

import android.content.Context;

import com.dreamteam.httprequest.Group.Entity.GroupData.Group;

public interface Router {
    void getGroup(Group group, Context context);
}
