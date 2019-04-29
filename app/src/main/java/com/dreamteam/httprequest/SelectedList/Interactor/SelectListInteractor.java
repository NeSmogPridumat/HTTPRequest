package com.dreamteam.httprequest.SelectedList.Interactor;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;

import com.dreamteam.httprequest.Data.ConstantConfig;
import com.dreamteam.httprequest.GroupList.Interactor.GroupsInteractor;
import com.dreamteam.httprequest.HTTPConfig;
import com.dreamteam.httprequest.HTTPManager;
import com.dreamteam.httprequest.Interfaces.SelectListHTTPManagerInterface;
import com.dreamteam.httprequest.SelectedList.Preseter.SelectListPresenter;
import com.dreamteam.httprequest.SelectedList.Protocols.SelectListPresenterInterface;

public class SelectListInteractor implements SelectListHTTPManagerInterface {

    SelectListPresenterInterface delegate;

    private HTTPConfig httpConfig = new HTTPConfig();
    private ConstantConfig constantConfig = new ConstantConfig();

    private HTTPManager httpManager = HTTPManager.get();

    public SelectListInteractor (SelectListPresenterInterface delegate){
        this.delegate = delegate;
    }

    public void getImage (final String id, final String imageURL){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String pathImage = httpConfig.serverURL + httpConfig.groupPORT +imageURL;
                httpManager.getRequest(pathImage,constantConfig.IMAGE_TYPE + ":" + id, SelectListInteractor.this);
            }
        }).start();
    }

    @Override
    public void response(byte[] byteArray, String type) {
        if (byteArray != null) {
            byte[] copyArray = byteArray;
            prepareGetBitmapOfByte(parsingStringType(type)[1], copyArray);
        }
    }

    @Override
    public void error(Throwable t) {

    }

    @Override
    public void errorHanding(int resposeCode) {

    }

    private String[] parsingStringType(String string){//--------------------------------------------разбор строки (getImageGroupType + ":" + groupID)
        String delimiter = ":";
        return string.split(delimiter);
    }

    private synchronized void prepareGetBitmapOfByte(final String groupID, byte[] byteArray){
        if (byteArray != null){
            Handler mainHandler = new Handler(Looper.getMainLooper());
            Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            if (bitmap != null) {
                bitmap = Bitmap.createScaledBitmap(bitmap, 150, 150, false);
            }
            final Bitmap finalBitmap = bitmap;

            byteArray = null;
            bitmap = null;
            Runnable myRunnable = new Runnable() {
                @Override
                public void run() {
                    delegate.answerGetImageGroups(groupID, finalBitmap);
                }
            };
            mainHandler.post(myRunnable);
        }
    }
}
