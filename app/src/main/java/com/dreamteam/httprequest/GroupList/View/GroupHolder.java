package com.dreamteam.httprequest.GroupList.View;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.dreamteam.httprequest.Group.Entity.GroupData.Group;
import com.dreamteam.httprequest.QueryPreferences;
import com.dreamteam.httprequest.R;

public class GroupHolder extends RecyclerView.ViewHolder {
    private TextView titleTextView, descriptionTextView, dateText;
    private ImageView imageView, adminImage;
    LinearLayout linearLayout;
    private CheckBox checkBox;
    private Bitmap imageData;
    private String userId;
    private Group group;

    GroupHolder(View item, String userId) {
        super(item);
        titleTextView = item.findViewById(R.id.list_item_group_title_text_view);
        descriptionTextView = item.findViewById(R.id.list_item_group_description_text_view);
        imageView = item.findViewById(R.id.group_image_view);
        linearLayout = item.findViewById(R.id.linear_layout_item);
        checkBox = item.findViewById(R.id.item_check_box);
        adminImage = item.findViewById(R.id.adminImage);
        this.userId = userId;
    }

    public void bindGroup(Group group){
        this.group = group;
        titleTextView.setText(group.personal.descriptive.title);
        descriptionTextView.setText(group.personal.descriptive.description);

        checkBox.setChecked(false);
//        imageData = group.content.mediaData.imageData;
        imageView.setImageBitmap(group.bitmap);

        if (group.admin.equals(userId)){
            adminImage.setVisibility(View.VISIBLE);
        } else {
            adminImage.setVisibility(View.GONE);
        }

        if (!group.status.equals("confirmed")){

        }
    }

    public String getGroupId (){
        return group.id;
    }

    public void setImage(Bitmap bitmap){
        imageView.setImageBitmap(bitmap);
    }
}
