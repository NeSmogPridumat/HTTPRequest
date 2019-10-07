package com.dreamteam.httprequest.AddOrEditInfoGroup;

import android.os.Bundle;

import com.dreamteam.httprequest.Interfaces.PresenterInterface;
import com.dreamteam.httprequest.MainActivity;

public class EditInfoGroupRouter {

    private MainActivity activity;

    public EditInfoGroupRouter(MainActivity activity){
        this.activity = activity;
    }

    public void showDialog(Bundle bundle, PresenterInterface delegate) {
        activity.showDialog(bundle, delegate);
    }

    public void answerDialogCamera (int responseCode, PresenterInterface delegate){
        activity.showFragment(delegate, responseCode);
    }

    public void answerDialogGallery (int responseCode, PresenterInterface delegate){
        activity.showFragment(delegate, responseCode);
    }

    public void answerDialogDeletePhoto (int responseCode, PresenterInterface delegate){
        activity.showFragment(delegate, responseCode);
    }

}
