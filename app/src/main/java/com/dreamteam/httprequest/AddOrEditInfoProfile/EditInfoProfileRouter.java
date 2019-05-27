package com.dreamteam.httprequest.AddOrEditInfoProfile;

import android.os.Bundle;

import com.dreamteam.httprequest.Interfaces.PresenterInterface;
import com.dreamteam.httprequest.MainActivity;

public class EditInfoProfileRouter {

    private MainActivity activity;

    public EditInfoProfileRouter(MainActivity activity){
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
