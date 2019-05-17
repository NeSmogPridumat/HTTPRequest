package com.dreamteam.httprequest.GroupList.View;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dreamteam.httprequest.Group.Entity.GroupData.Group;
import com.dreamteam.httprequest.R;

public class GroupHolder extends RecyclerView.ViewHolder {
    private TextView titleTextView, descriptionTextView;
    private ImageView imageView, adminImage;
    LinearLayout linearLayout;
    private CheckBox checkBox;
    private Bitmap imageData;

    GroupHolder(View item) {
        super(item);
        titleTextView = item.findViewById(R.id.list_item_group_title_text_view);
        descriptionTextView = item.findViewById(R.id.list_item_group_description_text_view);
        imageView = item.findViewById(R.id.group_image_view);
        linearLayout = item.findViewById(R.id.linear_layout_item);
        checkBox = item.findViewById(R.id.item_check_box);
        adminImage = item.findViewById(R.id.adminImage);
    }

    @SuppressLint("ResourceType")
    public void bindGroup(Group group){
        titleTextView.setText(group.content.simpleData.title);
        descriptionTextView.setText(group.content.simpleData.description);

        checkBox.setChecked(false);
            imageData = group.content.mediaData.imageData;
            imageView.setImageBitmap(imageData);



        if (group.rules == 7){
            adminImage.setVisibility(View.VISIBLE);
        }
    }
}
