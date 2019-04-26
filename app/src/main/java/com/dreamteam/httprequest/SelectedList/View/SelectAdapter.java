package com.dreamteam.httprequest.SelectedList.View;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dreamteam.httprequest.R;
import com.dreamteam.httprequest.SelectedList.SelectData;

import java.util.ArrayList;

public class SelectAdapter extends RecyclerView.Adapter<SelectHolder> {

    public ArrayList<SelectData> selectCollection;
    ArrayList<SelectHolder> selectHolders = new ArrayList<>();
    SparseBooleanArray checkArray = new SparseBooleanArray();


    public SelectAdapter(ArrayList<SelectData> selectCollection){
        this.selectCollection = selectCollection;
    }

    @NonNull
    @Override
    public SelectHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View view = layoutInflater.inflate(R.layout.selected_list_item, viewGroup, false);
        SelectHolder newSelectHolder = new SelectHolder(view);
        selectHolders.add(newSelectHolder);
        return newSelectHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SelectHolder selectHolder, int i) {
        SelectData selectData = selectCollection.get(i);
        selectHolder.checkBox.setChecked(checkArray.get(i));
        selectHolder.bindGroup(selectData);
    }

    @Override
    public int getItemCount() {
        int size = 0;
        if (selectCollection.size()!=0){
            size = selectCollection.size();
        }
        return size;
    }

    public  void changeItem(String id, Bitmap bitmap){
        for (int i = 0; i < selectCollection.size(); i ++){
            SelectData selectData = selectCollection.get(i);
            if (selectData.id.equals(id)){
                selectData.image = bitmap;
            }
            notifyItemChanged(i);
        }
    }
}
