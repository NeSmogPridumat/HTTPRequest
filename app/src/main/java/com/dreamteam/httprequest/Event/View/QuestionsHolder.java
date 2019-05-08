package com.dreamteam.httprequest.Event.View;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dreamteam.httprequest.Event.Entity.EventType4.Questions;
import com.dreamteam.httprequest.R;

public class QuestionsHolder extends RecyclerView.ViewHolder {

    public TextView titleTextView, descriptionTextView;
    public LinearLayout linearLayout;

    public QuestionsHolder(@NonNull View item) {
        super(item);
        titleTextView = item.findViewById(R.id.title_questions);
        descriptionTextView = item.findViewById(R.id.description_questions);
        linearLayout = item.findViewById(R.id.questions_button_linear_layout);
    }

    public void bindGroup(Questions questions){
        titleTextView.setText(questions.title);
        descriptionTextView.setText(questions.description);

    }
}
