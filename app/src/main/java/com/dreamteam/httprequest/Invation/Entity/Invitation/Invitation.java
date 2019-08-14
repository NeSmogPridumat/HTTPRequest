package com.dreamteam.httprequest.Invation.Entity.Invitation;

import com.dreamteam.httprequest.database.Data.InvitationDB;

public class Invitation {
    public String id;
    public String initiator;
    public String receiver;
    public String group;
    public long created;
    public String status;
    public String replieadAt;

    public InvitationDB initInvitationDB(){
        InvitationDB invitationDB = new InvitationDB();
        invitationDB.id = id;
        invitationDB.initiator = initiator;
        invitationDB.receiver = receiver;
        invitationDB.group = group;
        invitationDB.created = Long.toString(created);
        invitationDB.status = status;
        invitationDB.replieadAt = replieadAt;
        return invitationDB;
    }
}
