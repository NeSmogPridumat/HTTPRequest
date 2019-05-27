package com.dreamteam.httprequest.SelectedList.View;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import com.dreamteam.httprequest.R;
import com.dreamteam.httprequest.SelectedList.SelectData;

import java.util.ArrayList;

public class SelectAdapter extends RecyclerView.Adapter<SelectHolder> implements Filterable {

    ArrayList<SelectData> selectCollection;
    private ArrayList<SelectHolder> selectHolders = new ArrayList<>();
    private SparseBooleanArray checkArray = new SparseBooleanArray();
    ArrayList<SelectData> mFilteredList;
    ArrayList<SelectData> allObject;


    SelectAdapter(ArrayList<SelectData> selectCollection){
        this.selectCollection = selectCollection;
        mFilteredList = selectCollection;
        allObject = selectCollection;
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
        if (selectCollection != null && selectCollection.size()!=0){
            size = selectCollection.size();
        }
        return size;
    }

    void changeItem(String id, Bitmap bitmap){
        for (int i = 0; i < selectCollection.size(); i ++){
            SelectData selectData = selectCollection.get(i);
            if (selectData.id.equals(id)){
                selectData.image = bitmap;
            }
            notifyItemChanged(i);
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    mFilteredList = allObject;
                } else {
                    ArrayList<SelectData> filteredList = new ArrayList<>();
                    for (int i = 0; i < allObject.size(); i++) {
                        if (allObject.get(i).title.toLowerCase().contains(charString)) { // Сортируем по тексту из Formula
                            filteredList.add(allObject.get(i));
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
                selectCollection = (ArrayList<SelectData>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
}
