package com.dreamteam.httprequest.Group;

import android.os.Bundle;

import com.dreamteam.httprequest.AddOrEditInfoProfile.Data.InfoProfileData;
import com.dreamteam.httprequest.Data.ConstantConfig;
import com.dreamteam.httprequest.Data.RequestInfo;
import com.dreamteam.httprequest.Group.Presenter.GroupPresenter;
import com.dreamteam.httprequest.Group.Protocols.GroupPresenterInterface;
import com.dreamteam.httprequest.Interfaces.PresenterInterface;
import com.dreamteam.httprequest.MainActivity;
import com.dreamteam.httprequest.ObjectList.ObjectData;
import com.dreamteam.httprequest.SelectedList.Data.SelectData;

import java.util.ArrayList;

public class GroupRouter {

    private MainActivity activity;
    private ConstantConfig constantConfig = new ConstantConfig();

    public GroupRouter (MainActivity activity){
        this.activity = activity;
    }

    public void  showSelectList(ArrayList<SelectData> selectData, PresenterInterface delegate, String type){
        activity.openSelectList(selectData, delegate, type);
    }

    public void  showSelectList(PresenterInterface delegate){
        activity.openSelectList(delegate);
    }

    public void openGroup(String id){
        activity.getGroup(id);
    }

    public void openGroupsList(){
        activity.openGroupList();
    }

    public void showAddGroup(InfoProfileData infoProfileData, RequestInfo requestInfo, PresenterInterface delegate, String type){
        activity.openEditGroup(infoProfileData, requestInfo, delegate, type);
    }

    public void showMembersList(ArrayList<ObjectData> objectDataArrayList, PresenterInterface delegate, String type){
        activity.openObjectList(objectDataArrayList, delegate, type);
    }

    public void openGroupAfterSelect (String id, int rules){
        activity.openGroupAfterSelect(id, rules);
    }

    public void exitGroup (){
        activity.onBackPressed();
    }

    public void openObjectList(ArrayList<ObjectData> objectDataArrayList, PresenterInterface delegate){
        activity.openObjectList(objectDataArrayList, delegate, constantConfig.GROUP_TYPE);
    }

    public void showDialog(String message, int[] photoActionArray, PresenterInterface delegate){
        //создается Bundle
        android.os.Bundle bundle = new Bundle();

        // в Bundle закидываем заголовок для списка
        bundle.putString("title", message);

        // в Bundle закидываем список вариантов
        bundle.putIntArray("array", photoActionArray);
        activity.showDialog(bundle, delegate);
    }

    public void openCreateDiscussionFragment(PresenterInterface delegate){
        Bundle bundle = new Bundle();
        bundle.putString("type", "discussion");
        int[] actionArray = {0b1000};
        bundle.putIntArray("array", actionArray);
        activity.showDialog(bundle, delegate);
    }

    public void openVoting (String idRatingEvent){
        activity.openVoting(idRatingEvent);
    }
}
