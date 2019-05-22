package com.dreamteam.httprequest.Event.View;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

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

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull final QuestionsHolder questionsHolder, final int i) {
        final Questions question = questions.get(i);
        questionsHolder.bindGroup(question);

        int linearCount = questions.get(i).answers.size()/2;
        for(int c = 0; c <= linearCount; c++ ){
            LinearLayout linearLayout = new LinearLayout(questionsHolder.linearLayout.getContext());
            linearLayout.setId(3000 + c);
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
            questionsHolder.linearLayout.addView(linearLayout);
        }

        if(linearCount%2 == 1){
            LinearLayout linearLayout = new LinearLayout(questionsHolder.linearLayout.getContext());
            linearLayout.setId(3000 + linearCount/2);
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        }

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1f);
        layoutParams.setMargins(20, 20, 20, 20);
        for (int j = 0; j < question.answers.size(); j++){
            int linear = j/2;
            questionsHolder.itemView.setHasTransientState(true);
            LinearLayout linearLayout = questionsHolder.itemView.findViewById(3000 + linear);
            Button button = new Button( questionsHolder.itemView.findViewById(3000 + linear).getContext());
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
                    eventType4Controller.resultToQuestions(question.id, question.answers.get(finalJ).value, userID);
                    eventType4Controller.position = Integer.parseInt(question.id);
                }
            });
            linearLayout.addView(button);
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

    private void animationOn(QuestionsHolder questionsHolder){
            Display display = eventType4Controller.getActivity().getWindowManager().getDefaultDisplay();
            ObjectAnimator animator = ObjectAnimator.ofFloat(questionsHolder.linearLayout, "translationX",1000);
            animator.setDuration(10000);
            animator.start();
        }
    }

