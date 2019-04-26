package com.dreamteam.httprequest;

public class HTTPConfig {
    public String serverURL = "http://192.168.0.100:";
    public String serverPORT = "8888";
    public String userPORT = serverPORT;
    public String groupPORT = serverPORT;

    // REQUEST TYPE PATH
    public String reqUser = "/user";
    public String reqGroup = "/group";

    public String USERS = "/users";
    public String USER = "/user";
    public String MAIN_USER = "/mainuser";
    public String CREATOR_GROUP  = "/creatorgroup";
    public String GROUP = "/group";
    public String GROUPS = "/groups";
    public String RULES = "/rules";
    public String ADMIN = "/admin";
    public String ORGANIZATION = "/organization";
    public String GROUP_BY_USER = "/groupbyuser";
    public String USER_BY_GROUP = "/userbygroup";
    public String GROUP_USER    =  "/groupuser";
    public String ORGANIZATION_GROUP = "/organizationgroup";
    public String ORGANIZATION_BY_GROUP = "/organizationbygroup";
    public String GROUP_BY_ORGANIZATION = "/groupbyorganization";
    public String AUTH = "/auth";
    public String SMS_KEY = "/smskey";
    public String REFRESH = "/refresh";
    public String CREATE = "/create";

    public String ENABLE = "/enable";
    public String DISABLE = "/disable";
    public String PARENT = "/parent";
    public String CHILD = "/child";
    public String EVENT_SYSTEM  = "/eventsystem";
    public String EVENT  = "/event";
    public String EVENT_LINK  = "/eventlink";
    public String EVENT_USER_LINK  = "/event/user";
    public String EVENT_GROUP_LINK = "/event/group";

    public String DEL = "/del";
    public String ID_PARAM = "?id=";
    public String EVENT_ID_PARAM = "?eventID=";
    public String USER_ID_PARAM = "?userID=";
    public String GROUP_ID_PARAM = "?groupID=";
    public String GET = "/get";
    public String ADD = "/add";
}
