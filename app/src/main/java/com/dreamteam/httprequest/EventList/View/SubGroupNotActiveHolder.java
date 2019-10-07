package com.dreamteam.httprequest.EventList.View;

import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dreamteam.httprequest.Group.Entity.GroupData.Group;
import com.dreamteam.httprequest.R;

public class SubGroupNotActiveHolder extends RecyclerView.ViewHolder {

    private TextView titleTextView, dateTextView, descriptionTextView;
    LinearLayout linearLayout;
    public Button confirmedButton, deniedButton;
    private View view;

    public SubGroupNotActiveHolder(@NonNull View item) {
        super(item);
        view = item;
        titleTextView = item.findViewById(R.id.event_list_title_text_view);
        descriptionTextView = item.findViewById(R.id.event_list_description_text_view);
        linearLayout = item.findViewById(R.id.event_button_linear_layout);
        confirmedButton = item.findViewById(R.id.confirmedButtom);
        deniedButton = item.findViewById(R.id.deniedButtom);

    }

    public void bindGroup(Group group){
        titleTextView.setText("Активация подгруппы");
//        dateTextView.setText(new SimpleDateFormat("hh:mm MM.dd.yyyy").format(event.date));
        descriptionTextView.setText(view.getResources().getString(R.string.сonfirm_the_creation_of_a_subgroup) + group.personal.descriptive.title);
    }
}
