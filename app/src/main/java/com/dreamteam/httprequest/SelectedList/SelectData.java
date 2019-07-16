package com.dreamteam.httprequest.SelectedList;

import android.graphics.Bitmap;

import com.dreamteam.httprequest.Group.Entity.GroupData.Group;
import com.dreamteam.httprequest.User.Entity.UserData.User;

public class SelectData {
    public String title;
    public String description;
    public Bitmap image;
    public String imageURL;
    public String id;
    public Boolean check = false;
    public int rules;

    public SelectData initFromUser(User user){
        this.id = user.id;
        this.title = user.content.simpleData.name;
        this.description = user.content.simpleData.surname;
        this.image = user.content.mediaData.bitmap;
        this.imageURL = user.content.mediaData.image;
        //this.rules = user.rules;
        return this;
    }

    public SelectData initFromGroup (Group group){
        this.id = group.id;
        this.title = group.content.simpleData.title;
        this.description = group.content.simpleData.description;
        this.image = group.content.mediaData.imageData;
        this.imageURL = group.content.mediaData.image;
        return this;
    }
}

