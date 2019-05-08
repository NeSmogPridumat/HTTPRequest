package com.dreamteam.httprequest.Event.View;

import android.content.ClipData;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.dreamteam.httprequest.Event.Entity.EventType4.Questions;
import com.dreamteam.httprequest.R;

import java.util.ArrayList;

public class QuestionsAdapter extends RecyclerView.Adapter<QuestionsHolder> {

    private ArrayList<Questions> questions;
    private ArrayList<QuestionsHolder> eventHolders = new ArrayList<>();
    private String userID;

    private EventType4Controller eventType4Controller;

    public QuestionsAdapter(ArrayList<Questions> questions, String userID, EventType4Controller eventType4Controller) {
        this.userID = userID;
        this.questions = questions;
        this.eventType4Controller = eventType4Controller;
    }

    @NonNull
    @Override
    public QuestionsHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View view = layoutInflater.inflate(R.layout.questions_item, viewGroup, false);
        QuestionsHolder newEventHolder = new QuestionsHolder(view);
        eventHolders.add(newEventHolder);
        return newEventHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionsHolder questionsHolder, final int i) {
        final Questions question = questions.get(i);
        questionsHolder.bindGroup(question);

        for (int j = 0; j < question.answers.size(); j++){
            questionsHolder.itemView.setHasTransientState(true);
            Button button = new Button(questionsHolder.linearLayout.getContext());
            button.setId(2000 + j);
            button.setText(question.answers.get(j).title);
            button.setTextSize(12f);
            final int finalJ = j;
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    eventType4Controller.resultToQuestions(question.id, question.answers.get(finalJ).value, userID);
                    eventType4Controller.position = Integer.parseInt(question.id);
                }
            });
            questionsHolder.linearLayout.addView(button);
        }
    }

    @Override
    public int getItemCount() {
        int size = 0;
        if (questions.size()!=0){
            size = questions.size();
        }
        return size;
    }
}
