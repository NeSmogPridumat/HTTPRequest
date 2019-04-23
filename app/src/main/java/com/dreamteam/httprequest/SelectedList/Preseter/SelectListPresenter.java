package com.dreamteam.httprequest.SelectedList.Preseter;

import com.dreamteam.httprequest.Interfaces.PresenterInterface;
import com.dreamteam.httprequest.MainActivity;
import com.dreamteam.httprequest.SelectedList.Protocols.SelectListPresenterInterface;
import com.dreamteam.httprequest.SelectedList.SelectListData;
import com.dreamteam.httprequest.SelectedList.SelectListRouter;

import java.util.ArrayList;

public class SelectListPresenter implements SelectListPresenterInterface {

    private MainActivity activity;
    private SelectListRouter selectListRouter;


    public SelectListPresenter(MainActivity activity){
        this.activity = activity;
        selectListRouter = new SelectListRouter(activity);
    }

    //отправляем в делегате список выбранных объектов
    public void inputSelect (PresenterInterface delegate, ArrayList<SelectListData> arrayList, String type){
        delegate.inputSelect(arrayList, type);
    }

}
