package com.dreamteam.httprequest.Event.View;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.dreamteam.httprequest.Data.RequestInfo;
import com.dreamteam.httprequest.Event.Entity.Event;
import com.dreamteam.httprequest.Event.Presenter.EventPresenter;
import com.dreamteam.httprequest.Event.Protocols.EventViewInterface;
import com.dreamteam.httprequest.R;

/**
 * A simple {@link Fragment} subclass.
 */
@SuppressLint("ValidFragment")
public class EventController extends Fragment implements EventViewInterface {

    private Event event;

    private TextView titleTextView, descriptionTextView;
    private Button okType1Button, okType2Botton, cancelButton;
    private FrameLayout frameLayout;

    private EventPresenter eventPresenter = new EventPresenter(this);


    public EventController(Event event) {
        // Required empty public constructor
        this.event = event;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_event_controller, container, false);
        okType1Button = view.findViewById(R.id.type1_event_button);
        okType2Botton = view.findViewById(R.id.ok_type2_event_button);
        cancelButton = view.findViewById(R.id.cancel_event_button);
        titleTextView = view.findViewById(R.id.event_title_text_view);
        titleTextView.setText(event.data.content.simpleData.title);
        descriptionTextView = view.findViewById(R.id.event_description_text_view);
        descriptionTextView.setText(event.data.content.simpleData.description);
        frameLayout = view.findViewById(R.id.event_frame_layout);
        if (event.response.type == 1){
            okType1Button.setVisibility(View.VISIBLE);
            okType1Button = view.findViewById(R.id.type1_event_button);
        } else if (event.response.type == 2){
            frameLayout.setVisibility(View.VISIBLE);
        }
        return view;
    }

    @Override
    public void onStart() {
        okType1Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                event.data.result = true;
                event.active = false;
                eventPresenter.answerEvent(event);
            }
        });

        okType2Botton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Event newEvent = new Event();
//                newEvent.data.result = true;
//                newEvent.id = event.id;
                event.data.result = true;
                event.active = false;
                eventPresenter.answerEvent(event);
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        super.onStart();
    }
}
