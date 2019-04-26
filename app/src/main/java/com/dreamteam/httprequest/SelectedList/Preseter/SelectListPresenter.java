package com.dreamteam.httprequest.SelectedList.Preseter;

import android.graphics.Bitmap;

import com.dreamteam.httprequest.Interfaces.PresenterInterface;
import com.dreamteam.httprequest.MainActivity;
import com.dreamteam.httprequest.SelectedList.Interactor.SelectListInteractor;
import com.dreamteam.httprequest.SelectedList.Protocols.SelectListPresenterInterface;
import com.dreamteam.httprequest.SelectedList.Protocols.SelectListViewController;
import com.dreamteam.httprequest.SelectedList.SelectData;
import com.dreamteam.httprequest.SelectedList.SelectListRouter;

import java.util.ArrayList;

public class SelectListPresenter implements SelectListPresenterInterface {
    private SelectListInteractor selectListInteractor = new SelectListInteractor(this);

    private MainActivity activity;
    private SelectListRouter selectListRouter;
    private SelectListViewController delegate;


    public SelectListPresenter(SelectListViewController delegate, MainActivity activity){
        this.activity = activity;
        this.delegate = delegate;
        selectListRouter = new SelectListRouter(activity);
    }

    //отправляем в делегате список выбранных объектов
    public void inputSelect (PresenterInterface delegate, ArrayList<SelectData> arrayList, String type){
        delegate.inputSelect(arrayList, type);
    }

    public void getImage (String id, String imageURL){
        selectListInteractor.getImage(id, imageURL);
    }

    @Override
    public void answerGetImageGroups(String objectID, Bitmap finalBitmap){
        delegate.redrawAdapter(objectID, finalBitmap);
    }

}
