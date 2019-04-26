package com.dreamteam.httprequest.ObjectList.View;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.dreamteam.httprequest.ObjectList.ObjectData;
import com.dreamteam.httprequest.R;

public class ObjectListHolder extends RecyclerView.ViewHolder {

  private TextView titleTextView, descriptionTextView;
  public ImageView imageView;
  public LinearLayout linearLayout;

  ObjectData objectData = new ObjectData();

  public ObjectListHolder(@NonNull View item) {
    super(item);
    titleTextView = item.findViewById(R.id.object_list_title_text_view);
    descriptionTextView = item.findViewById(R.id.object_list_description_text_view);
    imageView = item.findViewById(R.id.object_list_image_view);
  }

  public void bindGroup(ObjectData objectData){
    this.objectData = objectData;
    titleTextView.setText(objectData.title);
    descriptionTextView.setText(objectData.description);

    Bitmap imageData = objectData.imageData;
    imageView.setImageBitmap(imageData);
  }
}
