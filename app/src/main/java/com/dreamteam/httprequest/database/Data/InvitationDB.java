package com.dreamteam.httprequest.database.Data;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.dreamteam.httprequest.Invation.Entity.Invitation.Invitation;

@Entity
public class InvitationDB {
    @PrimaryKey
    @NonNull
    public String id;
    public String initiator = null;
    public String receiver = null;
    public String group = null;
    public String created = null;
    public String status = null;
    public String replieadAt = null;
    //public Bitmap bitmap;


    public Invitation initUser(){
        Invitation invitation = new Invitation();
        invitation.id = id;
        invitation.created = new Long(created);
        invitation.initiator = initiator;
        invitation.receiver = receiver;
        invitation.group = group;
        invitation.status  = status;
        invitation.replieadAt = replieadAt;
        return invitation;
    }
}
