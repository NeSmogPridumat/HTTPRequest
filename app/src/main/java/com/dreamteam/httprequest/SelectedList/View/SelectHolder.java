package com.dreamteam.httprequest.SelectedList.View;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dreamteam.httprequest.Group.Entity.GroupData.Group;
import com.dreamteam.httprequest.R;
import com.dreamteam.httprequest.SelectedList.SelectListData;

public class SelectHolder extends RecyclerView.ViewHolder {

    private TextView titleTextView, descriptionTextView;
    public ImageView imageView;
    public LinearLayout linearLayout;
    public CheckBox checkBox;

    private SelectListData selectListData;


    public SelectHolder(@NonNull View item) {
        super(item);
        titleTextView = item.findViewById(R.id.select_list_title_text_view);
        descriptionTextView = item.findViewById(R.id.select_list_description_text_view);
        imageView = item.findViewById(R.id.select_list_image_view);
        checkBox = item.findViewById(R.id.select_list_checkbox);
    }

    public void bindGroup(SelectListData selectListData){
        this.selectListData = selectListData;
        titleTextView.setText(selectListData.title);
        descriptionTextView.setText(selectListData.description);

        checkBox.setChecked(selectListData.check);

        Bitmap imageData = selectListData.image;
        imageView.setImageBitmap(imageData);
    }
}
