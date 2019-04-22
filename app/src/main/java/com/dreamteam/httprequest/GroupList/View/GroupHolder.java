package com.dreamteam.httprequest.Group.View;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dreamteam.httprequest.Group.Entity.GroupData.Group;
import com.dreamteam.httprequest.Group.Presenter.GroupPresenter;
import com.dreamteam.httprequest.HTTPConfig;
import com.dreamteam.httprequest.R;
import com.squareup.picasso.Picasso;

public class GroupHolder extends RecyclerView.ViewHolder {
    private TextView titleTextView, descriptionTextView;
    public ImageView imageView;

    private Group group;
    HTTPConfig httpConfig = new HTTPConfig();

    public GroupHolder(View item) {
        super(item);

        titleTextView = item.findViewById(R.id.list_item_group_title_text_view);
        descriptionTextView = item.findViewById(R.id.list_item_group_description_text_view);

        imageView = item.findViewById(R.id.group_image_view);

    }

    public void bindGroup(Group group){
        this.group = group;
        titleTextView.setText(group.content.simpleData.title);
        descriptionTextView.setText(group.content.simpleData.description);

        imageView.setImageBitmap(group.content.mediaData.imageData);
    }

}
