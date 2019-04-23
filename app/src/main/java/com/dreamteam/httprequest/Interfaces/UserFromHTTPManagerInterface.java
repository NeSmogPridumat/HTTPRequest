package com.dreamteam.httprequest.Interfaces;

import org.json.JSONObject;

public interface UserFromHTTPManagerInterface extends OutputHTTPManagerInterface {

    void response(byte[] byteArray, String type);

    void error(Throwable t);
}
