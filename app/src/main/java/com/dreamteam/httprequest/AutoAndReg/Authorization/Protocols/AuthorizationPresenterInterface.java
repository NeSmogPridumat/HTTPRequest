package com.dreamteam.httprequest.AutoAndReg.Authorization.Protocols;

import com.dreamteam.httprequest.AutoAndReg.Authorization.Entity.AuthDataObject;
import com.dreamteam.httprequest.AutoAndReg.Authorization.Entity.InfoAndTokenData;
import com.dreamteam.httprequest.AutoAndReg.Authorization.Entity.Token;
import com.dreamteam.httprequest.Interfaces.PresenterInterface;

public interface AuthorizationPresenterInterface extends PresenterInterface {

    void answerCreateLogin (String answer, AuthDataObject authDataObject);

    void answerEnableUserAuth(boolean answer, AuthDataObject authDataObject);

    void answerCreateUserToAuth(boolean answer);

    void answerGetUserToken (InfoAndTokenData infoAndTokenData);

    void errorHanding(int responseCode);

    void error (Throwable t);
}
