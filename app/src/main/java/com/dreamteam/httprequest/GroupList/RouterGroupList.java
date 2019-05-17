package com.dreamteam.httprequest.GroupList;

import com.dreamteam.httprequest.AddOrEditInfoProfile.InfoProfileData;
import com.dreamteam.httprequest.Interfaces.PresenterInterface;
import com.dreamteam.httprequest.MainActivity;
import com.dreamteam.httprequest.SelectedList.SelectData;

import java.util.ArrayList;

public class RouterGroupList {
    MainActivity activity;

    public RouterGroupList (MainActivity activity){
        this.activity = activity;
    }

    //показать список групп
    public void showGroupList(){
        activity.openGroupList();
    }

    //показать список с checkBox
    public void showSelectList(ArrayList<SelectData> selectData, PresenterInterface delegate, MainActivity activity, String type){
        activity.openSelectList(selectData,delegate, type);
    }

    public void openGroup(String id, int rules){
        activity.openGroup(id, rules);
    }

    public void showAddGroup(InfoProfileData infoProfileData, PresenterInterface delegate, String type){
        activity.openEditProfile(infoProfileData,null, delegate, type);
    }

}

