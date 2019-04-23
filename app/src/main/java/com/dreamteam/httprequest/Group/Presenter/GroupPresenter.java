package com.dreamteam.httprequest.Group.Presenter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.dreamteam.httprequest.Group.Entity.GroupData.Group;
import com.dreamteam.httprequest.Group.Interactor.GroupInteractor;
import com.dreamteam.httprequest.Group.Protocols.GroupPresenterInterface;
import com.dreamteam.httprequest.Group.Protocols.GroupViewInterface;
import com.dreamteam.httprequest.GroupList.Protocols.GroupsPresenterInterface;
import com.dreamteam.httprequest.GroupList.Protocols.GroupsViewInterface;
import com.dreamteam.httprequest.GroupList.Protocols.Router;

public class GroupPresenter implements GroupPresenterInterface {
    private GroupViewInterface delegate;
    private Router myRouter;
    private GroupInteractor groupInteractor = new GroupInteractor(this);


    public GroupPresenter(GroupViewInterface delegate){
        this.delegate = delegate;
    }

    //============================ОТПРАВКА В INTERACTOR==========================================//

    public void getGroup(String id){
        groupInteractor.getGroup(id);
        Log.i("GROUP_PRESENTER", id);

    }

    @Override
    public void error(String error) {
        delegate.error(error);
    }

    @Override
    public void answerGetGroup(String title, String description) {
        delegate.outputGroupView(title, description);
    }

    @Override
    public void answerGetImage(Bitmap bitmap) {
        delegate.outputImageView(bitmap);
    }

    @Override
    public void answerGetMembers(int members) {
        delegate.outputMembersView(members);
    }

    public void openGroup(Group group, Router myRouter, Context context) {
        myRouter.getGroup(group, context);
        myRouter=null;
    }
}
