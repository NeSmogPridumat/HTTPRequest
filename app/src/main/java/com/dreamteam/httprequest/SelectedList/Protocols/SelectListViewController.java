package com.dreamteam.httprequest.SelectedList.Protocols;

import android.graphics.Bitmap;

public interface SelectListViewController {

    void redrawAdapter(String objectID, Bitmap bitmap);

    void error (Throwable t);
}
