package com.dreamteam.httprequest.EventList.View;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dreamteam.httprequest.EventList.Entity.Event;
import com.dreamteam.httprequest.R;

import java.util.ArrayList;

public class EventAdapter extends RecyclerView.Adapter<EventHolder> {

    public ArrayList<Event> eventArrayList;
    ArrayList<EventHolder> eventHolders = new ArrayList<>();

    public EventAdapter (ArrayList<Event> eventArrayList){
        this.eventArrayList = eventArrayList;
    }

    @NonNull
    @Override
    public EventHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View view = layoutInflater.inflate(R.layout.list_item_event, viewGroup, false);
        EventHolder newEventHolder = new EventHolder(view);
        eventHolders.add(newEventHolder);
        return newEventHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull EventHolder eventHolder, int i) {
        Event event = eventArrayList.get(i);
        eventHolder.bindGroup(event);
    }

    @Override
    public int getItemCount() {
        int size = 0;
        if (eventArrayList.size()!=0){
            size = eventArrayList.size();
        }
        return size;
    }
}
