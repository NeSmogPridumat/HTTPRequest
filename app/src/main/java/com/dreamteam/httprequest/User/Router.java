package com.dreamteam.httprequest.User;

import android.graphics.Bitmap;
import android.os.Bundle;

import com.dreamteam.httprequest.AddOrEditInfoProfile.InfoProfileData;
import com.dreamteam.httprequest.Interfaces.PresenterInterface;
import com.dreamteam.httprequest.MainActivity;
import com.dreamteam.httprequest.User.Entity.UserData.User;

public class Router {

    MainActivity activity;

    public Router (MainActivity activity){
        this.activity = activity;
    }

    public void showEditInfoProfile(InfoProfileData infoProfileData, PresenterInterface delegate, String type){
        activity.openEditProfile(infoProfileData, delegate, type);
    }

    public void showUser (User user){
        activity.showUser(user);
    }

    public void openUser(){
        activity.openProfile();
    }

//    public void  changeFragment(Fragment fragment){
//        activity.openGroup(data);
////        activity.changeFragment(fragment, null);
//    }
}
