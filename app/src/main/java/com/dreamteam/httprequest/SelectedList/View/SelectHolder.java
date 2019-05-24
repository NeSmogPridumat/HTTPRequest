package com.dreamteam.httprequest.SelectedList.View;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.dreamteam.httprequest.R;
import com.dreamteam.httprequest.SelectedList.SelectData;

public class SelectHolder extends RecyclerView.ViewHolder {

    private TextView titleTextView, descriptionTextView;
    private ImageView imageView;
    CheckBox checkBox;

    SelectHolder(@NonNull View item) {
        super(item);
        titleTextView = item.findViewById(R.id.select_list_title_text_view);
        descriptionTextView = item.findViewById(R.id.select_list_description_text_view);
        imageView = item.findViewById(R.id.select_list_image_view);
        checkBox = item.findViewById(R.id.select_list_checkbox);
    }

    public void bindGroup(SelectData selectData){
        titleTextView.setText(selectData.title);
        descriptionTextView.setText(selectData.description);

        checkBox.setChecked(selectData.check);

        Bitmap imageData = selectData.image;
        imageView.setImageBitmap(imageData);
    }
}
