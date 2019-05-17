package com.dreamteam.httprequest.SelectedList;

import com.dreamteam.httprequest.MainActivity;

public class SelectListRouter {

    private MainActivity activity;

    public SelectListRouter (MainActivity activity){
        this.activity = activity;
    }

    public void showGroupList(){
        activity.openGroupList();
    }
}
