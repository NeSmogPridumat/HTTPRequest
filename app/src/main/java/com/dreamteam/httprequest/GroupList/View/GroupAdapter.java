package com.dreamteam.httprequest.Group.View;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.dreamteam.httprequest.Group.Entity.GroupData.Group;
import com.dreamteam.httprequest.Group.Presenter.GroupPresenter;
import com.dreamteam.httprequest.R;

import java.util.ArrayList;

public class GroupAdapter extends RecyclerView.Adapter<GroupHolder>{
    public ArrayList<Group> groupCollection;


    public GroupAdapter(ArrayList<Group> groupCollection){
        this.groupCollection = groupCollection;
    }

    public  void changeItem(String groupID, Bitmap bitmap){
        for (int i = 0; i < groupCollection.size(); i ++){
            Group group = groupCollection.get(i);
            if (group.id.equals(groupID)){
//                group.content.simpleData.title = group.content.simpleData.title + "Update";
                group.content.mediaData.imageData = bitmap;
            }
        }
        this.notifyItemRangeChanged(0, groupCollection.size());
    }

    @NonNull
    @Override
    public GroupHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, final int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View view = layoutInflater.inflate(R.layout.list_item_group, viewGroup, false);
        return new GroupHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupHolder groupsHolder, int i) {
        Group group = groupCollection.get(i);
//        Context context = groupsHolder.imageView.getContext();
//        groupsHolder.bindGroup(group, groupIDThis, bitmapThis);
        groupsHolder.bindGroup(group);

    }

    @Override
    public int getItemCount() {
        return groupCollection.size();
    }
}

