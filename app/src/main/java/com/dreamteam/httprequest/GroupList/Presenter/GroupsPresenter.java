package com.dreamteam.httprequest.GroupList.Presenter;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.dreamteam.httprequest.Group.Entity.GroupData.Group;
import com.dreamteam.httprequest.Group.View.GroupController;
import com.dreamteam.httprequest.GroupList.Interactor.GroupInteractor;
import com.dreamteam.httprequest.GroupList.Protocols.GroupPresenterInterface;
import com.dreamteam.httprequest.GroupList.Protocols.GroupViewInterface;
import com.dreamteam.httprequest.Router;

import java.util.ArrayList;

public class GroupPresenter implements GroupPresenterInterface {

    private GroupViewInterface delegate;
    private GroupInteractor groupInteractor = new GroupInteractor(this);

    public GroupPresenter(GroupViewInterface delegate){
        this.delegate = delegate;
    }

    @Override
    public void answerGetImageGroups(String groupID, Bitmap bitmap) {
        delegate.redrawAdapter(groupID, bitmap);
    }

    @Override
    public void answerGetGroups(ArrayList<Group> groupCollection) {
        delegate.outputGroupsView(groupCollection);
    }

    @Override
    public void error(String error) {

    }

    public void getGroups(String id) {
        groupInteractor.getGroups(id);
    }


    public void openGroup(Group group, Router delegate){
        delegate.getGroup(group);
        Log.i("GROUP_PRESENTER", group.content.simpleData.title + " " + group.content.simpleData.description);

    }
}


