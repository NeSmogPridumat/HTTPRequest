package com.dreamteam.httprequest.Group.View;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dreamteam.httprequest.Event.Entity.EventType4.EventType4;
import com.dreamteam.httprequest.EventList.View.EventHolder;
import com.dreamteam.httprequest.R;

import java.util.ArrayList;

public class EventForGroupAdapter extends RecyclerView.Adapter<EventForGroupHolder>{
    ArrayList<EventType4> eventArrayList;
    private ArrayList<EventForGroupHolder> eventHolders = new ArrayList<>();

    EventForGroupAdapter(ArrayList<EventType4> eventArrayList){
        this.eventArrayList = eventArrayList;
    }

    @NonNull
    @Override
    public EventForGroupHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View view = layoutInflater.inflate(R.layout.list_item_event, viewGroup, false);
        EventForGroupHolder newEventHolder = new EventForGroupHolder(view);
        eventHolders.add(newEventHolder);
        return newEventHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull EventForGroupHolder eventForGroupHolder, int i) {
        EventType4 event = eventArrayList.get(i);
        eventForGroupHolder.bindGroup(event);
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
