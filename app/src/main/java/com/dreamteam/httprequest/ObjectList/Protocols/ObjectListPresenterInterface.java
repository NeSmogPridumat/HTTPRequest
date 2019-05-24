package com.dreamteam.httprequest.ObjectList.Protocols;

import android.graphics.Bitmap;

public interface ObjectListPresenterInterface {

  void answerGetImageGroups (String id, Bitmap bitmap);

  void error (String title, String description);
}
