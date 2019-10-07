package com.dreamteam.httprequest.Interfaces;

public interface OutputHTTPManagerInterface {

    void response(byte [] byteArray, String type);

    void error(Throwable t);

    void errorHanding(int responseCode, String type);
}
