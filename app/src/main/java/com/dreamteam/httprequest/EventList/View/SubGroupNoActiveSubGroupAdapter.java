package com.dreamteam.httprequest.EventList.View;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dreamteam.httprequest.Data.ConstantConfig;
import com.dreamteam.httprequest.Group.Entity.GroupData.Group;
import com.dreamteam.httprequest.R;

import java.util.ArrayList;

public class SubGroupNoActiveSubGroupAdapter extends RecyclerView.Adapter<SubGroupNotActiveHolder> {

    private ArrayList<SubGroupNotActiveHolder> subGroupNotActiveHolders = new ArrayList<>();
    private ArrayList<Group> subGroups = new ArrayList<>();
    private EventListController eventListController;
    private ConstantConfig constantConfig = new ConstantConfig();

    SubGroupNoActiveSubGroupAdapter(ArrayList<Group> subGroups, EventListController eventListController){
        this.eventListController = eventListController;
        this.subGroups = subGroups;
    }
    @NonNull
    @Override
    public SubGroupNotActiveHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View view = layoutInflater.inflate(R.layout.list_item_event, viewGroup, false);
        SubGroupNotActiveHolder newEventHolder = new SubGroupNotActiveHolder(view);
        subGroupNotActiveHolders.add(newEventHolder);
        return newEventHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SubGroupNotActiveHolder holder, int position) {
        final Group group = subGroups.get(position);
        holder.bindGroup(group);
        holder.confirmedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventListController.activatedSubGroup(group.id, constantConfig.CONFIRMED);
            }
        });
        holder.deniedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventListController.activatedSubGroup(group.id, constantConfig.DENIED);
            }
        });
    }

    @Override
    public int getItemCount() {
        int size = 0;
        if (subGroups.size() != 0){
            size = subGroups.size();
        }
        return size;
    }
}
