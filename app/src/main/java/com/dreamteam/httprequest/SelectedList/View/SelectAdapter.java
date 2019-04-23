package com.dreamteam.httprequest.SelectedList.View;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dreamteam.httprequest.Group.Entity.GroupData.Group;
import com.dreamteam.httprequest.GroupList.View.GroupHolder;
import com.dreamteam.httprequest.R;
import com.dreamteam.httprequest.SelectedList.SelectListData;

import java.util.ArrayList;

public class SelectAdapter extends RecyclerView.Adapter<SelectHolder> {

    public ArrayList<SelectListData> selectCollection;
    ArrayList<SelectHolder> selectHolders = new ArrayList<>();
    SparseBooleanArray checkArray = new SparseBooleanArray();


    public SelectAdapter(ArrayList<SelectListData> selectCollection){
        this.selectCollection = selectCollection;
    }

    @NonNull
    @Override
    public SelectHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View view = layoutInflater.inflate(R.layout.selected_list_item_group, viewGroup, false);
        SelectHolder newSelectHolder = new SelectHolder(view);
        selectHolders.add(newSelectHolder);
        return newSelectHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SelectHolder selectHolder, int i) {
        SelectListData selectListData = selectCollection.get(i);
        selectHolder.checkBox.setChecked(checkArray.get(i));
        selectHolder.bindGroup(selectListData);
//        if(selectCollection.get(i).check){
//            selectHolder.checkBox.setChecked(checkArray.get(i));
//        } else {
//            selectHolder.checkBox.setChecked(false);
//        }
    }

    @Override
    public int getItemCount() {
        int size = 0;
        if (selectCollection.size()!=0){
            size = selectCollection.size();
        }
        return size;
    }
}
