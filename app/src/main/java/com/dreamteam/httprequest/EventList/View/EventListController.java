package com.dreamteam.httprequest.EventList.View;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dreamteam.httprequest.Event.Entity.AnswerQuestion.AnswerQuestion;
import com.dreamteam.httprequest.Event.Entity.AnswerQuestion.AnswerQuestionResult;
import com.dreamteam.httprequest.Event.Entity.EventType4.EventType4;
import com.dreamteam.httprequest.EventList.Presenter.EventListPresenter;
import com.dreamteam.httprequest.EventList.Protocols.EventListViewInterface;
import com.dreamteam.httprequest.Group.Entity.GroupData.Group;
import com.dreamteam.httprequest.GroupList.View.RecyclerItemClickListener;
import com.dreamteam.httprequest.Invation.Entity.Invitation.Invitation;
import com.dreamteam.httprequest.MainActivity;
import com.dreamteam.httprequest.QueryPreferences;
import com.dreamteam.httprequest.R;
import com.dreamteam.httprequest.database.Data.InvitationDB;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 */
public class EventListController extends Fragment implements EventListViewInterface {

    private RecyclerView eventsRecyclerView, activateSubgroupRecyclerView;
    private MainActivity activity;
    private InvitationAdapter adapter;
    private SubGroupNoActiveSubGroupAdapter activateSubgroupAdapter;
    private String userID;
    private RelativeLayout progressBarOverlay;
    private ProgressBar progressBar;
    private RadioButton activeEventRadio, inactiveEventRadio;

    private EventListPresenter eventListPresenter;

    private ArrayList<EventType4> activeEvent;
    private ArrayList<EventType4> notActiveEvent;

    public EventListController() {
        // Required empty public constructor
    }

    public static EventListController newInstance(String userID){
        Bundle args = new Bundle();
        args.putString("userID", userID);
        EventListController fragment = new EventListController();
        fragment.setArguments(args);
        return fragment;
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

        activateSubgroupRecyclerView = view.findViewById(R.id.events_not_active_recycler_view);
        activateSubgroupRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        progressBarOverlay = view.findViewById(R.id.progressBarOverlay);
        progressBar = view.findViewById(R.id.progressBar);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        userID = getArguments().getString("userID");
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
        eventListPresenter.getInvations();
        eventListPresenter.getSubGroupNotActive();

        eventListPresenter.getEvents();
        eventListPresenter.getNotification(QueryPreferences.getUserIdPreferences(getContext()));


        activeEventRadio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventsRecyclerView.setVisibility(View.VISIBLE);
                activateSubgroupRecyclerView.setVisibility(View.GONE);
            }
        });

        inactiveEventRadio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activateSubgroupRecyclerView.setVisibility(View.VISIBLE);
                eventsRecyclerView.setVisibility(View.GONE);
            }
        });
        eventListPresenter.getRatingEvent();

//        eventListPresenter.getEvents(userID);

//        eventsRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(),
//                eventsRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
//            @Override
//            public void onItemClick(View view, int position) {
//                eventListPresenter.openEvent(activeEvent.get(position));
//            }
//
//            @Override
//            public void onLongItemClick(View view, int position) {
//
//            }
//        }));
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

//        adapter = new InvitationAdapter(activeEvent, userID, this);
        //nAdapter = new InvitationAdapter(notActiveEvent);

//        adapter.eventArrayList = activeEvent;
        eventsRecyclerView.setAdapter(adapter);
        eventsRecyclerView.getAdapter().notifyDataSetChanged();

//        nAdapter.eventArrayList = notActiveEvent;
  //      noteventsRecyclerView.setAdapter(nAdapter);
//        noteventsRecyclerView.getAdapter().notifyDataSetChanged();
        progressBarOverlay.setVisibility(View.GONE);


    }

    @Override
    public void error(Throwable t) {
        String title = null;
        String description  = null;
        if (t instanceof SocketTimeoutException || t instanceof ConnectException) {
            title = getContext().getResources().getString(R.string.error_connecting_to_server);
            description = getActivity().getResources()
                    .getString(R.string.check_the_connection_to_the_internet);
        }else if (t instanceof NullPointerException) {
            title = getActivity().getResources().getString(R.string.object_not_found);
            description = "";
        }
        Toast.makeText(activity, title + "\n" + description, Toast.LENGTH_LONG).show();
        progressBarOverlay.setVisibility(View.GONE);
    }

    @Override
    public void answerGetInvitations(List<Invitation> invitationArrayList) {
        if(invitationArrayList!= null) {
            adapter = new InvitationAdapter(invitationArrayList, this);
            eventsRecyclerView.setAdapter(adapter);
            eventsRecyclerView.getAdapter().notifyDataSetChanged();
            progressBarOverlay.setVisibility(View.GONE);


//        adapter = new InvitationAdapter(activeEvent, userID, this);
            //nAdapter = new InvitationAdapter(notActiveEvent);

//        adapter.eventArrayList = activeEvent;
            eventsRecyclerView.setAdapter(adapter);
            eventsRecyclerView.getAdapter().notifyDataSetChanged();

//        nAdapter.eventArrayList = notActiveEvent;
            //      noteventsRecyclerView.setAdapter(nAdapter);
//        noteventsRecyclerView.getAdapter().notifyDataSetChanged();
            progressBarOverlay.setVisibility(View.GONE);

            activeEventRadio.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    eventsRecyclerView.setVisibility(View.VISIBLE);
                    activateSubgroupRecyclerView.setVisibility(View.GONE);
                }
            });

            inactiveEventRadio.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activateSubgroupRecyclerView.setVisibility(View.VISIBLE);
                    eventsRecyclerView.setVisibility(View.GONE);
                }
            });
        } else {
            eventsRecyclerView.getAdapter();
//            eventsRecyclerView.removeItemDecorationAt(0);
        }
    }

    @Override
    public void answerGetSubGroupsNotActive(ArrayList<Group> subGroups) {
        activateSubgroupAdapter = new SubGroupNoActiveSubGroupAdapter(subGroups, this);
        activateSubgroupRecyclerView.setAdapter(activateSubgroupAdapter);
        activateSubgroupRecyclerView.getAdapter().notifyDataSetChanged();
        progressBarOverlay.setVisibility(View.GONE);
    }

    public void setAnswerInvited(String invitationDBId, String answer){
        eventListPresenter.setAnswerInvited(invitationDBId, answer);
    }

    public void resultToQuestions(String questionID, int questionValue, String userID){
//        AnswerQuestion answerQuestion = new AnswerQuestion();
//        AnswerQuestionResult answerQuestionResult = new AnswerQuestionResult();
//        answerQuestion.eventID = event.id;
//        answerQuestionResult.id = questionID;
//        answerQuestionResult.userID = userID;
//        answerQuestionResult.value = questionValue;
//        answerQuestion.result.add(answerQuestionResult);
//        answerQuestion.userID = activity.userID;
//        eventPresenter.resultToQuestion(answerQuestion);
    }

    public void activatedSubGroup(String group, String answer){
        eventListPresenter.activatedSubGroup(group, answer);
    }
}
