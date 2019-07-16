package com.dreamteam.httprequest.EventList.View;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.dreamteam.httprequest.Event.Entity.AnswerQuestion.AnswerQuestion;
import com.dreamteam.httprequest.Event.Entity.AnswerQuestion.AnswerQuestionResult;
import com.dreamteam.httprequest.Event.Entity.EventType4.EventType4;
import com.dreamteam.httprequest.Event.Entity.EventType4.Questions;
import com.dreamteam.httprequest.Event.View.EventType4Controller;
import com.dreamteam.httprequest.R;

import java.util.ArrayList;

public class EventAdapter extends RecyclerView.Adapter<EventHolder> {

    ArrayList<EventType4> eventArrayList;
    private ArrayList<EventHolder> eventHolders = new ArrayList<>();
    private String userID;

    private EventListController eventListController;

    EventAdapter(ArrayList<EventType4> eventArrayList,String userID,
                 EventListController eventListController){
        this.eventArrayList = eventArrayList;
        this.userID = userID;
        this.eventListController = eventListController;
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
        EventType4 event = eventArrayList.get(i);
        eventHolder.bindGroup(event);

        final Questions question = event.response.questions.get(i);
        //с помощью циклов программно создаем LinearLayout и кнопки для соответствующего кол-ва ответов
        int linearCount = event.response.questions.get(i).answers.size()/2;
        for(int c = 0; c <= linearCount; c++ ){
            LinearLayout linearLayout = new LinearLayout(eventHolder.linearLayout.getContext());
            linearLayout.setId(3000 + c);
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
            eventHolder.linearLayout.addView(linearLayout);
        }

        if(linearCount%2 == 1){
            LinearLayout linearLayout = new LinearLayout(eventHolder.linearLayout.getContext());
            linearLayout.setId(3000 + linearCount/2);
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        }

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1f);
        layoutParams.setMargins(20, 20, 20, 20);
        for (int j = 0; j < question.answers.size(); j++){
            int linear = j/2;
            eventHolder.itemView.setHasTransientState(true);
            LinearLayout linearLayout = eventHolder.itemView.findViewById(3000 + linear);
            Button button = new Button( eventHolder.itemView.findViewById(3000 + linear).getContext());
            button.setId(2000 + j);
            button.setText(question.answers.get(j).title);
            button.setTextSize(12f);
            if (question.answers.size()%2 == 1 && j == question.answers.size()-1) {
                LinearLayout.LayoutParams layoutParamsLast = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT, 1f);
                layoutParamsLast.setMargins(200, 0, 200, 0);
                button.setLayoutParams(layoutParamsLast);
            }else {
                button.setLayoutParams(layoutParams);
            }
            final int finalJ = j;
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    eventListController.resultToQuestions(question.id, question.answers.get(finalJ).value, userID);
                   //eventListController.position = Integer.parseInt(question.id);
                }
            });
            linearLayout.addView(button);
        }
    }

    @Override
    public int getItemCount() {
        int size = 0;
        if (eventArrayList.size()!=0){
            size = eventArrayList.size();
        }
        return size;
    }

//    public void resultToQuestions(String questionID, int questionValue, String userID){
//        AnswerQuestion answerQuestion = new AnswerQuestion();
//        AnswerQuestionResult answerQuestionResult = new AnswerQuestionResult();
//        answerQuestion.eventID = .id;
//        answerQuestionResult.id = questionID;
//        answerQuestionResult.userID = userID;
//        answerQuestionResult.value = questionValue;
//        answerQuestion.result.add(answerQuestionResult);
//        answerQuestion.userID = activity.userID;
//        eventPresenter.resultToQuestion(answerQuestion);
//    }
}
