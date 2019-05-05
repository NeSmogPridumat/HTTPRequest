package com.dreamteam.httprequest.EventList.View;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dreamteam.httprequest.Event.Entity.Event;
import com.dreamteam.httprequest.R;

public class EventHolder extends RecyclerView.ViewHolder {

    public TextView titleTextView, descriptionTextView;
    public ImageView imageView;

    private Event event;

    public EventHolder(@NonNull View item) {
        super(item);
        titleTextView = item.findViewById(R.id.event_list_title_text_view);
        descriptionTextView = item.findViewById(R.id.event_list_description_text_view);
    }

    public void bindGroup(Event event){
        this.event = event;
        titleTextView.setText(event.data.content.simpleData.title);
        descriptionTextView.setText(event.data.content.simpleData.description);

    }
}
