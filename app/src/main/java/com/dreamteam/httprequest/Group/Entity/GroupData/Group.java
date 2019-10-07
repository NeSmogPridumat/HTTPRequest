package com.dreamteam.httprequest.Group.Entity.GroupData;

import android.graphics.Bitmap;

import com.dreamteam.httprequest.database.Data.GroupDB;
import com.google.gson.Gson;

import java.util.ArrayList;

public class Group {

    public String id;
    public String creator;
    public String admin;
    public String date;
    public String status;
    public Bitmap bitmap;

    public Personal personal;
    public NodeData nodeData;
    public ArrayList<String> members;

//    @Override
//    public int compareTo(Group o) {
//        return personal.descriptive.title.compareTo(personal.descriptive.title);
//    }

    public GroupDB initGroupDB (){
        Gson gson = new Gson();
        GroupDB groupDB = new GroupDB();
        groupDB.id = id;
        groupDB.creator = creator;
        groupDB.admin = admin;
        groupDB.date = date;
        groupDB.children = gson.toJson(nodeData.children);
        groupDB.parent = nodeData.parent;
        groupDB.title = personal.descriptive.title;
        groupDB.members = gson.toJson(members);
        groupDB.organization = nodeData.organization;
        groupDB.status = status;
        return groupDB;
    }
}
