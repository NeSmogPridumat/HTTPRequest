package com.dreamteam.httprequest.EventList.View;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dreamteam.httprequest.EventList.Entity.Event;
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

    private RecyclerView eventsRecyclerView;
    private MainActivity activity;
    private EventAdapter adapter;
    private String userID;

    private EventListPresenter eventListPresenter;

    private ArrayList<Event> eventArrayList = new ArrayList<>();


    public EventListController(String userID) {
        // Required empty public constructor
        this.eventArrayList = eventArrayList;
        this.userID = userID;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_event_list, container, false);
        eventsRecyclerView = view.findViewById(R.id.events_recycler_view);
        eventsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        eventListPresenter.getEvents(userID);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setHasOptionsMenu(true);
        activity = (MainActivity) getActivity();

        adapter = new EventAdapter(eventArrayList);
        eventListPresenter = new EventListPresenter(this, activity);
//        activity.setActionBarTitle(type);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        adapter.eventArrayList = eventArrayList;
        eventsRecyclerView.setAdapter(adapter);

        eventsRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), eventsRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
//                objectListPresenter.openObjectProfile(arrayList.get(position), type);
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));
//        getImage(arrayList);
        super.onStart();
    }

    @Override
    public void answerGetEvents(final ArrayList<Event> eventArrayList) {
        Collections.reverse(eventArrayList);
        ArrayList<Event> activeEvent = new ArrayList<>();
        ArrayList<Event> notActiveEvent = new ArrayList<>();
        for (int i = 0; i < eventArrayList.size(); i++){
            if (eventArrayList.get(i).active == true){
                activeEvent.add(eventArrayList.get(i));
            } else {
                notActiveEvent.add(eventArrayList.get(i));
            }
        }
        if (activeEvent.size() != 0) {
            activity.bottomNavigationTextView.setVisibility(View.VISIBLE);
            activity.bottomNavigationTextView.setText(Integer.toString(activeEvent.size()));

        } else {
            activity.bottomNavigationTextView.setVisibility(View.INVISIBLE);

        }
        this.eventArrayList = eventArrayList;
        adapter.eventArrayList = eventArrayList;
        eventsRecyclerView.setAdapter(adapter);
        final Context context = getContext();

        eventsRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), eventsRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                MainActivity activityAction = (MainActivity) getActivity();
                activityAction = (MainActivity)context;


            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));
    }
}
