package com.dreamteam.httprequest.SelectList.Presenter;

import android.graphics.Bitmap;

import com.dreamteam.httprequest.Interfaces.PresenterInterface;
import com.dreamteam.httprequest.SelectList.Protocol.SelectView;
import com.dreamteam.httprequest.SelectList.SelectInteractor.SelectInteractor;
import com.dreamteam.httprequest.SelectedList.Data.SelectData;
import com.dreamteam.httprequest.User.Entity.UserData.User;
import java.util.ArrayList;

public class SelectPresenter implements com.dreamteam.httprequest.SelectList.Protocol.SelectPresenter {

    private SelectView delegate;
    private SelectInteractor selectInteractor = new SelectInteractor(this);

    public SelectPresenter(SelectView delegate){
        this.delegate = delegate;
    }

    public void getUsers(String name){
        selectInteractor.getUsers(name);
    }

    @Override
    public void answerGetUsers(ArrayList<User> users) {
        delegate.answerGetUsers(users);
    }

    @Override
    public void answerGetImageGroups(String groupID, Bitmap bitmap) {
        delegate.redrawAdapter(groupID, bitmap);
    }

    public void inputSelect (PresenterInterface delegate, ArrayList<SelectData> arrayList, String type){
        delegate.inputSelect(arrayList, type);
    }
}
