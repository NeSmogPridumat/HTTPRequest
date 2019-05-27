package com.dreamteam.httprequest.GroupList.View;

import android.animation.ObjectAnimator;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import com.dreamteam.httprequest.Group.Entity.GroupData.Group;
import com.dreamteam.httprequest.R;

import java.util.ArrayList;

public class GroupAdapter extends RecyclerView.Adapter<GroupHolder> implements Filterable {
    ArrayList<Group> groupCollection = new ArrayList<>();
    private ArrayList<GroupHolder> groupHolders = new ArrayList<>();

    ArrayList<Group> mFilteredList = new ArrayList<>();
    ArrayList<Group> allGroup;

    GroupAdapter(ArrayList<Group> groupCollection){
        this.groupCollection = groupCollection;
        mFilteredList = groupCollection;

    }

    //приходящие позже картинки сравниваются по id группы и присваиваются
    void changeItem(String groupID, Bitmap bitmap){
        for (int i = 0; i < groupCollection.size(); i ++){
            Group group = groupCollection.get(i);
            if (group.id.equals(groupID)){
                group.content.mediaData.imageData = bitmap;
            }
            notifyItemChanged(i);
        }
    }

    @NonNull
    @Override
    public GroupHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, final int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View view = layoutInflater.inflate(R.layout.list_item_group, viewGroup, false);
        GroupHolder newGroupHolder = new GroupHolder(view);
        groupHolders.add(newGroupHolder);
        return newGroupHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull GroupHolder groupsHolder, int i) {
        Group group = groupCollection.get(i);
        groupsHolder.bindGroup(group);
    }

    @Override
    public int getItemCount() {
        int size = 0;
        if (groupCollection != null && groupCollection.size()!=0){
            size = groupCollection.size();
        }
        return size;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();

                if (charString.isEmpty()) {
                    mFilteredList = allGroup;
                } else {
                    ArrayList<Group> filteredList = new ArrayList<>();
                    for (int i = 0; i < allGroup.size(); i++) {
                        if (allGroup.get(i).content.simpleData.title.toLowerCase().contains(charString)) { // Сортируем по тексту из Formula
                            filteredList.add(allGroup.get(i));
                        }
                    }
                    mFilteredList = filteredList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = mFilteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                groupCollection = (ArrayList<Group>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
    //анимация сдвига holder'ов вправо
    public void animationOn(){
        for (int i = 0; i < groupHolders.size(); i++) {
            ObjectAnimator animator = ObjectAnimator.ofFloat(groupHolders.get(i).linearLayout, "translationX", 100f);
            animator.setDuration(500);
            animator.start();
        }
    }

    //анимация сдвига holder'ов влево
    void animationBack(){
        for (int i = 0; i < groupHolders.size(); i++) {
            ObjectAnimator animator = ObjectAnimator.ofFloat(groupHolders.get(i).linearLayout, "translationX", 0f);
            animator.setDuration(500);
            animator.start();
        }
    }
}

