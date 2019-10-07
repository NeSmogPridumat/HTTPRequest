package com.dreamteam.httprequest.database.Data;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.dreamteam.httprequest.Group.Entity.GroupData.Group;
import com.dreamteam.httprequest.Group.Entity.GroupData.NodeData;
import com.dreamteam.httprequest.Group.Entity.GroupData.Personal;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

@Entity
public class NoActiveGroupDB {

    @PrimaryKey
    @NonNull
    public String id;
    public String title;
    public String creator;
    public String admin;
    public String status;
    public String date;
    public String parent;
    public String children;
    public String members;
    public String organization;

    public Group initGroup() {
        Gson gson = new Gson();
        Group group = new Group();
        group.personal = new Personal();
        group.nodeData = new NodeData();
        group.id = id;
        group.admin = admin;
        group.creator = creator;
        group.personal.descriptive.title = title;
        group.nodeData.parent = parent;
        if (members != null) {
            group.members = gson.fromJson(members, new TypeToken<ArrayList<String>>() {
            }.getType());
        }
        if (children != null) {
            group.nodeData.children = gson.fromJson(children, new TypeToken<ArrayList<String>>() {
            }.getType());
        }
        return group;
    }
}
