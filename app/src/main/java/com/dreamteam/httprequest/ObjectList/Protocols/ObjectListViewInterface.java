package com.dreamteam.httprequest.ObjectList.Protocols;

import android.graphics.Bitmap;

public interface ObjectListViewInterface {

  void redrawAdapter(String groupID, Bitmap bitmap);

  void error (String title, String description);
}
