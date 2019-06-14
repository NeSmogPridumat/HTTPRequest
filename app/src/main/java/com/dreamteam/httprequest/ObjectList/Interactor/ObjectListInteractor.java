package com.dreamteam.httprequest.ObjectList.Interactor;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;

import com.dreamteam.httprequest.Data.ConstantConfig;
import com.dreamteam.httprequest.Data.HTTPConfig;
import com.dreamteam.httprequest.HTTPManager.HTTPManager;
import com.dreamteam.httprequest.Interfaces.ObjectListFromHTTPManagerInterface;
import com.dreamteam.httprequest.ObjectList.Protocols.ObjectListPresenterInterface;

public class ObjectListInteractor implements ObjectListFromHTTPManagerInterface {

  private ObjectListPresenterInterface delegate;

  private HTTPConfig httpConfig = new HTTPConfig();
  private ConstantConfig constantConfig = new ConstantConfig();

  private HTTPManager httpManager = HTTPManager.get();

  public ObjectListInteractor (ObjectListPresenterInterface delegate){
    this.delegate = delegate;
  }

  public void getImage (final String id, final String imageURL){
    new Thread(new Runnable() {
      @Override
      public void run() {
        String pathImage = httpConfig.serverURL + httpConfig.SERVER_GETTER + imageURL;
        httpManager.getRequest(pathImage,constantConfig.IMAGE_TYPE + ":" + id,
                ObjectListInteractor.this);
      }
    }).start();
  }

  @Override public void response(byte[] byteArray, String type) {
      if (byteArray != null) {
          prepareGetBitmapOfByte(parsingStringType(type)[1], byteArray);
      }
  }

  private synchronized void prepareGetBitmapOfByte(final String groupID, byte[] byteArray){
    if (byteArray != null){
      Handler mainHandler = new Handler(Looper.getMainLooper());
      Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
      if (bitmap != null) {
        bitmap = Bitmap.createScaledBitmap(bitmap, 150, 150, false);
      }
      final Bitmap finalBitmap = bitmap;

      Runnable myRunnable = new Runnable() {
        @Override
        public void run() {
          delegate.answerGetImageGroups(groupID, finalBitmap);
        }
      };
      mainHandler.post(myRunnable);
    }
  }

  @Override public void error(final Throwable t) {
      Handler mainHandler = new Handler(Looper.getMainLooper());
      Runnable myRunnable = new Runnable() {
        @Override
        public void run() {
          delegate.error(t);
        }
      };
      mainHandler.post(myRunnable);
  }

  @Override
  public void errorHanding(int resposeCode, String type) {

  }

  private String[] parsingStringType(String string){//--------------------------------------------разбор строки (getImageGroupType + ":" + groupID)
        String delimiter = ":";
        return string.split(delimiter);
    }
}
