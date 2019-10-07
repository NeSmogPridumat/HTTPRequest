package com.dreamteam.httprequest.SelectedList.View;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dreamteam.httprequest.R;
import com.dreamteam.httprequest.SelectedList.Data.SelectData;

public class SelectHolder extends RecyclerView.ViewHolder {

    private TextView titleTextView, descriptionTextView;
    private ImageView imageView;
    CheckBox checkBox;

    SelectHolder(@NonNull View item) {
        super(item);
        titleTextView = item.findViewById(R.id.select_list_title_text_view);
        imageView = item.findViewById(R.id.select_list_image_view);
        checkBox = item.findViewById(R.id.select_list_checkbox);
    }

    public void bindGroup(SelectData selectData){
        titleTextView.setText(selectData.title + " " + selectData.description);
//        descriptionTextView.setText(selectData.description);

        checkBox.setChecked(selectData.check);

        Bitmap imageData = selectData.image;
        imageView.setImageBitmap(imageData);
    }
}
