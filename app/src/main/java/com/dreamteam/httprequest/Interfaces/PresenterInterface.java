package com.dreamteam.httprequest.Interfaces;

import android.graphics.Bitmap;

import com.dreamteam.httprequest.AddOrEditInfoProfile.InfoProfileData;
import com.dreamteam.httprequest.Data.RequestInfo;
import com.dreamteam.httprequest.SelectedList.SelectData;

import java.util.ArrayList;

public interface PresenterInterface  {
    void showDialog();
    void answerDialog(int i);
    void forResult(Bitmap bitmap);
    void inputSelect(ArrayList<SelectData> arrayList, String type);
    void editInfo(InfoProfileData infoProfileData, RequestInfo requestInfo, String type);
}
