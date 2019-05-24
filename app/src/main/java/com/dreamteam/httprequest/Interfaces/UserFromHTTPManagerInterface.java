package com.dreamteam.httprequest.Interfaces;

public interface UserFromHTTPManagerInterface extends OutputHTTPManagerInterface {

    void response(byte[] byteArray, String type);

    void error(Throwable t);
}
