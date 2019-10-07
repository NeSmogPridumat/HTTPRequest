package com.dreamteam.httprequest.EventList.View;

import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dreamteam.httprequest.Event.Entity.EventType4.EventType4;
import com.dreamteam.httprequest.Invation.Entity.Invitation.Invitation;
import com.dreamteam.httprequest.R;
import com.dreamteam.httprequest.database.Data.InvitationDB;

import java.text.SimpleDateFormat;

public class EventHolder extends RecyclerView.ViewHolder {

    private TextView titleTextView, dateTextView, descriptionTextView;
    LinearLayout linearLayout;
    public Button confirmedButton, deniedButton;
    private View view;

    EventHolder(@NonNull View item) {
        super(item);
        titleTextView = item.findViewById(R.id.event_list_title_text_view);
        descriptionTextView = item.findViewById(R.id.event_list_description_text_view);
        linearLayout = item.findViewById(R.id.event_button_linear_layout);
        confirmedButton = item.findViewById(R.id.confirmedButtom);
        deniedButton = item.findViewById(R.id.deniedButtom);
        view = item;
    }


    public void bindGroup(Invitation invitation){
        titleTextView.setText(view.getResources().getString(R.string.add_to_group));
        descriptionTextView.setText("Пользователь " + invitation.initiator + " добавляет " + invitation.receiver + " в группу " + invitation.group);
    }
}
