package com.dreamteam.httprequest.SelectedList.Data;

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
        this.title = user.personal.descriptive.name;
        this.description = user.personal.descriptive.surname;
        //this.image = user.content.mediaData.bitmap;
        //this.imageURL = user.content.mediaData.image;
        //this.rules = user.rules;
        return this;
    }

    public SelectData initFromGroup (Group group){
        this.id = group.id;
        this.title = group.personal.descriptive.title;
        this.description = group.personal.descriptive.description;
        //this.image = group.content.mediaData.imageData;
        //this.imageURL = group.content.mediaData.image;
        return this;
    }
}

