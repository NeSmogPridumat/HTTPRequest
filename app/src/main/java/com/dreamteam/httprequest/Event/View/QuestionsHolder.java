package com.dreamteam.httprequest.Event.View;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dreamteam.httprequest.Event.Entity.EventType4.Questions;
import com.dreamteam.httprequest.R;

public class QuestionsHolder extends RecyclerView.ViewHolder {

    private TextView titleTextView, descriptionTextView;
    LinearLayout linearLayout;

    QuestionsHolder(@NonNull View item) {
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
