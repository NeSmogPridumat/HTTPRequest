package com.dreamteam.httprequest.AutoAndReg.Authorization.Protocols;

import com.dreamteam.httprequest.AutoAndReg.Authorization.Entity.AuthDataObject;
import com.dreamteam.httprequest.Interfaces.PresenterInterface;

public interface AuthorizationPresenterInterface extends PresenterInterface {

    void answerCreateLogin (boolean answer, AuthDataObject authDataObject);

    void answerEnableUserAuth(boolean answer, AuthDataObject authDataObject);
}
