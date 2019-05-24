package com.dreamteam.httprequest.EventList.View;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

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
    private RelativeLayout progressBarOverlay;
    private ProgressBar progressBar;
    private RadioButton activeEventRadio, inactiveEventRadio;

    private EventListPresenter eventListPresenter;

    private ArrayList<EventType4> activeEvent;
    private ArrayList<EventType4> notActiveEvent;

    public EventListController(String userID) {
        // Required empty public constructor
        this.userID = userID;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_event_list, container, false);
        eventsRecyclerView = view.findViewById(R.id.events_recycler_view);
        eventsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        activeEventRadio = view.findViewById(R.id.activeEvent);
        inactiveEventRadio = view.findViewById(R.id.inactiveEvent);

        noteventsRecyclerView = view.findViewById(R.id.events_not_active_recycler_view);
        noteventsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        progressBarOverlay = view.findViewById(R.id.progressBarOverlay);
        progressBar = view.findViewById(R.id.progressBar);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (MainActivity) getActivity();
        eventListPresenter = new EventListPresenter(this, activity);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        notActiveEvent = new ArrayList<>();
        activeEvent = new ArrayList<>();
        activity.setActionBarTitle("Event List");
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.progress_rotate);
        progressBar.startAnimation(animation);
        progressBarOverlay.setVisibility(View.VISIBLE);
        eventListPresenter.getEvents(userID);

        eventsRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(),
                eventsRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                eventListPresenter.openEvent(activeEvent.get(position));
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));
        super.onStart();
    }

    @Override
    public void answerGetEvents(final ArrayList<EventType4> eventArrayList) {

        Collections.reverse(eventArrayList);
        for (int i = 0; i < eventArrayList.size(); i++){
            if (eventArrayList.get(i).active){
                activeEvent.add(eventArrayList.get(i));
            } else {
                if(notActiveEvent.size() < 20) {
                    notActiveEvent.add(eventArrayList.get(i));
                }
            }
        }

        adapter = new EventAdapter(activeEvent);
        nAdapter = new EventAdapter(notActiveEvent);

        adapter.eventArrayList = activeEvent;
        eventsRecyclerView.setAdapter(adapter);
        eventsRecyclerView.getAdapter().notifyDataSetChanged();

        nAdapter.eventArrayList = notActiveEvent;
        noteventsRecyclerView.setAdapter(nAdapter);
        noteventsRecyclerView.getAdapter().notifyDataSetChanged();
        progressBarOverlay.setVisibility(View.GONE);

        activeEventRadio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventsRecyclerView.setVisibility(View.VISIBLE);
                noteventsRecyclerView.setVisibility(View.GONE);
            }
        });

        inactiveEventRadio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noteventsRecyclerView.setVisibility(View.VISIBLE);
                eventsRecyclerView.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void error(String title, String description) {
        Toast.makeText(activity, title + "\n" + description, Toast.LENGTH_LONG).show();
        progressBar.setVisibility(View.GONE);
    }
}
