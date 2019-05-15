package com.dreamteam.httprequest.ObjectList.Presenter;

import android.graphics.Bitmap;

import com.dreamteam.httprequest.Data.ConstantConfig;
import com.dreamteam.httprequest.MainActivity;
import com.dreamteam.httprequest.ObjectList.Interactor.ObjectListInteractor;
import com.dreamteam.httprequest.ObjectList.ObjectData;
import com.dreamteam.httprequest.ObjectList.ObjectListRouter;
import com.dreamteam.httprequest.ObjectList.Protocols.ObjectListPresenterInterface;
import com.dreamteam.httprequest.ObjectList.Protocols.ObjectListViewInterface;

public class ObjectListPresenter implements ObjectListPresenterInterface {

  private ObjectListViewInterface delegate;
  private MainActivity activity;
  private ObjectListInteractor objectListInteractor = new ObjectListInteractor(this);
  private ObjectListRouter router;
  private ConstantConfig constantConfig = new ConstantConfig();

  public ObjectListPresenter (ObjectListViewInterface delegate, MainActivity activity){
    this.delegate = delegate;
    this.activity = activity;
    router = new ObjectListRouter(activity);
  }

  public void getImage (String id, String imageURL){
    objectListInteractor.getImage(id, imageURL);
  }

  @Override public void answerGetImageGroups(String id, Bitmap bitmap) {
    delegate.redrawAdapter(id, bitmap);
  }

  @Override
  public void error(String title, String description) {
    delegate.error(title, description);
  }

  public void openObjectProfile(ObjectData objectData, String type){
    if (type.equals(constantConfig.USER_TYPE)){
      router.showUserObjectList(objectData.id);
    }else if (type.equals(constantConfig.GROUP_TYPE)){
      router.showGroup(objectData.id, objectData.rules);
    }
  }
}
