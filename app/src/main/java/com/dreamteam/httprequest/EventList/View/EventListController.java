package com.dreamteam.httprequest.EventList.View;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dreamteam.httprequest.Event.Entity.EventType12.Event;
import com.dreamteam.httprequest.Event.Entity.EventType4.EventType4;
import com.dreamteam.httprequest.EventList.Presenter.EventListPresenter;
import com.dreamteam.httprequest.EventList.Protocols.EventListViewInterface;
import com.dreamteam.httprequest.GroupList.View.RecyclerItemClickListener;
import com.dreamteam.httprequest.MainActivity;
import com.dreamteam.httprequest.R;

import java.util.ArrayList;
import java.util.Collections;

/**
 * A simple {@link Fragment} subclass.
 */
@SuppressLint("ValidFragment")
public class EventListController extends Fragment implements EventListViewInterface {

    private RecyclerView eventsRecyclerView, noteventsRecyclerView;
    private MainActivity activity;
    private EventAdapter adapter, nAdapter;
    private String userID;

    private EventListPresenter eventListPresenter;

    private ArrayList<EventType4> eventArrayList = new ArrayList<>();
    private ArrayList<EventType4> activeEvent = new ArrayList<>();


    public EventListController(String userID) {
        // Required empty public constructor
        this.userID = userID;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_event_list, container, false);
        eventsRecyclerView = view.findViewById(R.id.events_recycler_view);
        eventsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()){
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        noteventsRecyclerView = view.findViewById(R.id.events_not_active_recycler_view);
        noteventsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()){
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setHasOptionsMenu(true);
        activity = (MainActivity) getActivity();

        adapter = new EventAdapter(eventArrayList);
        nAdapter = new EventAdapter(eventArrayList);
        eventListPresenter = new EventListPresenter(this, activity);
//        activity.setActionBarTitle(type);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
//        adapter.notifyDataSetChanged();
        eventListPresenter.getEvents(userID);
        adapter.eventArrayList = eventArrayList;
        eventsRecyclerView.setAdapter(adapter);


        eventsRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), eventsRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
//                objectListPresenter.openObjectProfile(arrayList.get(position), type);
                    eventListPresenter.openEvent(activeEvent.get(position));
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));
//        getImage(arrayList);
        super.onStart();
    }

    @Override
    public void answerGetEvents(final ArrayList<EventType4> eventArrayList) {
        Collections.reverse(eventArrayList);
        ArrayList<EventType4> notActiveEvent = new ArrayList<>();
        for (int i = 0; i < eventArrayList.size(); i++){
            if (eventArrayList.get(i).active){
                activeEvent.add(eventArrayList.get(i));
            } else {
                notActiveEvent.add(eventArrayList.get(i));
            }
        }
//        if (activeEvent.size() != 0) {
//            activity.bottomNavigationTextView.setVisibility(View.VISIBLE);
//            activity.bottomNavigationTextView.setText(Integer.toString(activeEvent.size()));
//
//        } else {
//            activity.bottomNavigationTextView.setVisibility(View.INVISIBLE);
//
//        }
        this.eventArrayList = eventArrayList;
        adapter.eventArrayList = activeEvent;
        eventsRecyclerView.setAdapter(adapter);

        nAdapter.eventArrayList = notActiveEvent;
        noteventsRecyclerView.setAdapter(nAdapter);
//        final Context context = getContext();

//        eventsRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), eventsRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
//            @Override
//            public void onItemClick(View view, int position) {
//
//                MainActivity activityAction = (MainActivity) getActivity();
//                activityAction = (MainActivity)context;
//                eventListPresenter.openEvent(eventArrayList.get(position));
//
//            }
//
//            @Override
//            public void onLongItemClick(View view, int position) {
//
//            }
//        }));
    }
}
