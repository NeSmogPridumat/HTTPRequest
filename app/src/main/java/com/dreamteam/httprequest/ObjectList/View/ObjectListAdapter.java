package com.dreamteam.httprequest.ObjectList.View;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.dreamteam.httprequest.ObjectList.ObjectData;
import com.dreamteam.httprequest.R;
import com.dreamteam.httprequest.SelectedList.SelectData;
import java.util.ArrayList;

public class ObjectListAdapter extends RecyclerView.Adapter<ObjectListHolder>  {

  public ArrayList<ObjectData> objectDataArrayList;
  ArrayList<ObjectListHolder> selectHolders = new ArrayList<>();

  public ObjectListAdapter( ArrayList<ObjectData> objectDataArrayList){
    this.objectDataArrayList = objectDataArrayList;
  }

  @NonNull @Override
  public ObjectListHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
    LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
    View view = layoutInflater.inflate(R.layout.object_list_item, viewGroup, false);
    ObjectListHolder newObjectHolder = new ObjectListHolder(view);
    selectHolders.add(newObjectHolder);
    return newObjectHolder;
  }

  @Override public void onBindViewHolder(@NonNull ObjectListHolder objectListHolder, int i) {
    ObjectData objectData = objectDataArrayList.get(i);
    objectListHolder.bindGroup(objectData);
  }

  @Override public int getItemCount() {
    int size = 0;
    if (objectDataArrayList.size()!=0){
      size = objectDataArrayList.size();
    }
    return size;
  }

  public  void changeItem(String id, Bitmap bitmap){
    for (int i = 0; i < objectDataArrayList.size(); i ++){
      ObjectData objectData = objectDataArrayList.get(i);
      if (objectData.id.equals(id)){
        objectData.imageData = bitmap;
      }
      notifyItemChanged(i);
    }
  }
}
