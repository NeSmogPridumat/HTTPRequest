package com.dreamteam.httprequest;

import org.json.JSONException;
import org.json.JSONObject;

public interface HTTPInterface {
    void getResponse(JSONObject id) throws JSONException;

}
