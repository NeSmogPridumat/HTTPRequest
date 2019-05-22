package com.dreamteam.httprequest.Event.View;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.dreamteam.httprequest.Event.Entity.AnswerQuestion.AnswerQuestion;
import com.dreamteam.httprequest.Event.Entity.AnswerQuestion.AnswerQuestionResult;
import com.dreamteam.httprequest.Event.Entity.EventType4.EventType4;
import com.dreamteam.httprequest.Event.Entity.EventType4.Questions;
import com.dreamteam.httprequest.Event.Presenter.EventPresenter;
import com.dreamteam.httprequest.Event.Protocols.EventViewInterface;
import com.dreamteam.httprequest.MainActivity;
import com.dreamteam.httprequest.R;
import com.dreamteam.httprequest.User.Entity.UserData.User;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
@SuppressLint("ValidFragment")
public class EventType4Controller extends Fragment implements EventViewInterface {

    private EventType4 event;
    private MainActivity activity;
    private EventPresenter eventPresenter;
    private QuestionsAdapter adapter;

    private ArrayList<User> users;

    private TextView titleTextView, descriptionTextView, userTextView;
    private RecyclerView questionsRecyclerView;

    public String userID;
    public int position;

    public EventType4Controller(EventType4 event) {
        // Required empty public constructor
        this.event = event;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_event_type4_controller, container, false);
        titleTextView = view.findViewById(R.id.event_type4_title_text_view);
        titleTextView.setText(event.response.content.simpleData.title);
        descriptionTextView = view.findViewById(R.id.event_type4_description_text_view);
        descriptionTextView.setText(event.response.content.simpleData.description);
        userTextView = view.findViewById(R.id.event_type4_name_user_text_view);
        questionsRecyclerView = view.findViewById(R.id.questions_recycler_view);
        questionsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity  = (MainActivity) getActivity();
        eventPresenter = new EventPresenter(this, activity);
        users = event.response.users;
    }

    @Override
    public void onStart() {
        createListForUser(event);
        super.onStart();
    }

    private boolean checkUser(User user){
        boolean check = false;
        for(int i = 0; i < user.questions.size(); i++){
            if (user.questions.get(i).active){
                check = true;
                break;
            }
        }
        return check;
    }

    @SuppressLint("SetTextI18n")
    private void createListForUser(EventType4 event) {

        if (event.response.type != 4) {
            for (int j = 0; j < event.response.questions.size(); j++) {
                ArrayList<Questions> questions = new ArrayList<>();
                questions.add(event.response.questions.get(j));
                adapter = new QuestionsAdapter(questions, null, this);
                questionsRecyclerView.setAdapter(adapter);
                break;
            }
        } else {
            int counter = 0;
            for (int i = 0; i < event.response.users.size(); i++) {
                if (checkUser(event.response.users.get(i))) {
                    ArrayList<Questions> questions = new ArrayList<>();
                    userID = event.response.users.get(i).id;
                    userTextView.setText("User: " + event.response.users.get(i).content.simpleData.name + " " + event.response.users.get(i).content.simpleData.surname);
                    for (int j = 0; j < event.response.users.get(i).questions.size(); j++) {
                        if (event.response.users.get(i).questions.get(j).active) {
                            questions.add(event.response.questions.get(j));
                            counter++;
                        }
                    }
                    adapter = new QuestionsAdapter(questions, event.response.users.get(i).id, this);
                    questionsRecyclerView.setAdapter(adapter);
                    break;
                }
            }
            if (counter == 0) {
                Toast.makeText(getContext(), "Благодарим за ответы", Toast.LENGTH_LONG);
                eventPresenter.openEventList();
            }
        }
    }

    public void resultToQuestions(String questionID, int questionValue, String userID){
        AnswerQuestion answerQuestion = new AnswerQuestion();
        AnswerQuestionResult answerQuestionResult = new AnswerQuestionResult();
        answerQuestion.eventID = event.id;
        answerQuestionResult.id = questionID;
        answerQuestionResult.userID = userID;
        answerQuestionResult.value = questionValue;
        answerQuestion.result.add(answerQuestionResult);
        answerQuestion.userID = activity.userID;
        eventPresenter.resultToQuestion(answerQuestion);
    }

    @Override
    public void answerServerToQuestion() {

        if (event.response.type != 4){
           eventPresenter.openEventList();
        }else {
            for (int i = 0; i < event.response.users.size(); i++) {
                for (int j = 0; j < event.response.users.get(i).questions.size(); j++) {
                    if (event.response.users.get(i).id.equals(userID) && event.response.users.get(i).questions.get(j).id == position) {
                        event.response.users.get(i).questions.get(j).active = false;
                    }
                }
            }
        }
        createListForUser(event);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void error(String title, String description) {
        Toast.makeText(activity, title + "\n" + description, Toast.LENGTH_LONG).show();
    }
}
