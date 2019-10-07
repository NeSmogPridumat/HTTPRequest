package com.dreamteam.httprequest.AddOrEditInfoProfile.Presenter;

import android.graphics.Bitmap;
import android.os.Bundle;

import com.dreamteam.httprequest.AddOrEditInfoProfile.EditInfoProfileRouter;
import com.dreamteam.httprequest.AddOrEditInfoProfile.Data.InfoProfileData;
import com.dreamteam.httprequest.AddOrEditInfoProfile.Protocols.EditInfoProfileViewInterface;
import com.dreamteam.httprequest.Data.RequestInfo;
import com.dreamteam.httprequest.Dialog.DialogConfig;
import com.dreamteam.httprequest.Interfaces.PresenterInterface;
import com.dreamteam.httprequest.MainActivity;
import com.dreamteam.httprequest.R;
import com.dreamteam.httprequest.SelectedList.Data.SelectData;

import java.util.ArrayList;

public class EditInfoProfilePresenter implements PresenterInterface {

    private EditInfoProfileViewInterface delegateView;
    private PresenterInterface delegate;

    private EditInfoProfileRouter router;

    private DialogConfig dialogConfig;
    private MainActivity activity;

    //создание списка вариантов для диалога

    public EditInfoProfilePresenter(MainActivity activity, EditInfoProfileViewInterface delegateView, PresenterInterface delegate){
        this.delegate = delegate;
        this.delegateView = delegateView;
        router = new EditInfoProfileRouter(activity);
        dialogConfig = new DialogConfig(activity);
        this.activity = activity;
    }

    //вызов диалогового окна
    @Override
    public void showDialog() {
        int[] photoActionArray = {dialogConfig.CAMERA_REQUEST_CODE, dialogConfig.GALLERY_REQUEST_CODE}; //, dialogConfig.DELETE_PHOTO_REQUEST_CODE - для удаления фото

        //создается Bundle
        Bundle bundle = new Bundle();

        // в Bundle закидываем заголовок для списка
        bundle.putString("title", activity.getResources().getString(R.string.select_an_action));

        // в Bundle закидываем список вариантов
        bundle.putIntArray("array", photoActionArray);

        router.showDialog(bundle, this);
    }

    //обработка ответа от диалога
    @Override
    public void answerDialog(int i, String title, String message, String priority) {
        if ((i & dialogConfig.CAMERA_REQUEST_CODE) != 0){
            router.answerDialogCamera(i, this);
        }else if ((i & dialogConfig.GALLERY_REQUEST_CODE) != 0){
            router.answerDialogGallery(i, this);
        } else if ((i & dialogConfig.DELETE_PHOTO_REQUEST_CODE) != 0) {
            router.answerDialogDeletePhoto(i, this);
        }
    }

    //получение картинки от камеры/галлереи
    @Override
    public void forResult(Bitmap bitmap) {
        delegateView.forResult(bitmap);
    }

    @Override
    public void inputSelect(ArrayList<SelectData> arrayList, String type) {

    }

    //отправка введенных данных на изменение
    public void editInfo (InfoProfileData infoProfileData, RequestInfo requestInfo, String type){
        delegate.editInfo(infoProfileData, requestInfo, type);
    }
}
