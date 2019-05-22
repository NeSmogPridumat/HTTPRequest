package com.dreamteam.httprequest.EventList.View;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.dreamteam.httprequest.Event.Entity.EventType4.EventType4;
import com.dreamteam.httprequest.R;

public class EventHolder extends RecyclerView.ViewHolder {

    private TextView titleTextView, descriptionTextView;

    EventHolder(@NonNull View item) {
        super(item);
        titleTextView = item.findViewById(R.id.event_list_title_text_view);
        descriptionTextView = item.findViewById(R.id.event_list_description_text_view);
    }

    public void bindGroup(EventType4 event){
        titleTextView.setText(event.response.content.simpleData.title);
        descriptionTextView.setText(event.response.content.simpleData.description);
    }
}
