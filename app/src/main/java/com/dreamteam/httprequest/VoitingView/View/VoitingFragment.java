package com.dreamteam.httprequest.VoitingView.View;


import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dreamteam.httprequest.Event.Entity.AnswersVoting.AnswersVoting;
import com.dreamteam.httprequest.Event.Entity.AnswersVoting.Estimate;
import com.dreamteam.httprequest.R;
import com.dreamteam.httprequest.User.Entity.UserData.User;
import com.dreamteam.httprequest.User.Protocols.VotingViewInterface;
import com.dreamteam.httprequest.VoitingView.Presenter.VotingPresenter;

/**
 * A simple {@link Fragment} subclass.
 */
public class VoitingFragment extends Fragment implements VotingViewInterface {

    private View view;
    private String idRatingEvent;
    private VotingPresenter votingPresenter;
    private ImageView userImage;
    private TextView userName, userSurname;
    private int countDiscipline, countProfessionalism, countEfficiency, countLoyalty = 0;
    private Button sendButton;
    private SeekBar seekBar1, seekBar2, seekBar3, seekBar4;
    private CheckBox radioButton1, radioButton2, radioButton3, radioButton4, radioButton5, radioButton6, radioButton7, radioButton8, radioButton9, radioButton10;
    private RadioGroup radioGroup1, radioGroup2, radioGroup3, radioGroup4, radioGroup5, radioGroup6, radioGroup7,radioGroup8,radioGroup9, radioGroup10, radioGroup11, radioGroup12, radioGroup13;
    private User user;

    public VoitingFragment() {
        // Required empty public constructor
    }

    public static VoitingFragment newInstance(String idRatingEvent){
        Bundle args = new Bundle();
        args.putString("idRatingEvent", idRatingEvent);
        VoitingFragment fragment = new VoitingFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_voiting, container, false);
        userImage = view.findViewById(R.id.user_image_view);
        userName = view.findViewById(R.id.user_name_text_view);
        sendButton = view.findViewById(R.id.send_button);
        radioGroup1 = view.findViewById(R.id.question_one_discipline_radio_group);
        radioGroup2 = view.findViewById(R.id.question_two_discipline_radio_group);
        radioGroup3 = view.findViewById(R.id.question_three_discipline_radio_group);
        radioGroup4 = view.findViewById(R.id.question_four_discipline_radio_group);
        radioGroup5 = view.findViewById(R.id.question_five_discipline_radio_group);
        radioGroup6 = view.findViewById(R.id.question_six_discipline_radio_group);
        radioGroup7 = view.findViewById(R.id.question_seven_discipline_radio_group);
        radioGroup8 = view.findViewById(R.id.question_eight_discipline_radio_group);
        radioGroup9 = view.findViewById(R.id.question_one_professionalism_radio_group);
        radioGroup10 = view.findViewById(R.id.question_two_professionalism_radio_group);
        radioGroup11 = view.findViewById(R.id.question_three_professionalism_radio_group);
        radioGroup12 = view.findViewById(R.id.question_four_professionalism_radio_group);
        radioGroup13 = view.findViewById(R.id.question_five_professionalism_radio_group);
        seekBar1 = view.findViewById(R.id.seekBar1);
        seekBar2 = view.findViewById(R.id.seekBar2);
        seekBar3 = view.findViewById(R.id.seekBar3);
        seekBar4 = view.findViewById(R.id.seekBar4);
        radioButton1 = view.findViewById(R.id.loyalty1);
        radioButton2 = view.findViewById(R.id.loyalty2);
        radioButton3 = view.findViewById(R.id.loyalty3);
        radioButton4 = view.findViewById(R.id.loyalty4);
        radioButton5 = view.findViewById(R.id.loyalty5);
        radioButton6 = view.findViewById(R.id.loyalty6);
        radioButton7 = view.findViewById(R.id.loyalty7);
        radioButton8 = view.findViewById(R.id.loyalty8);
        radioButton9 = view.findViewById(R.id.loyalty9);
        radioButton10 = view.findViewById(R.id.loyalty10);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        idRatingEvent = getArguments().getString("idRatingEvent");
        votingPresenter = new VotingPresenter(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        votingPresenter.getUsersForVoting(idRatingEvent);
        super.onStart();
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean result = (radioGroup1.getCheckedRadioButtonId() == R.id.one1);
                Log.i("jdglksdhglksdhgl/ksdhg", Boolean.toString(result));
                boolean check = checkAnswers();
                if (!check){
                    Toast.makeText(getContext(), "Вы ответили не на все вопросы", Toast.LENGTH_LONG).show();
                } else {
                    treatmentAnswers();
                }
            }
        });

    }

    @Override
    public void answerGetUser(User user) {
        userName.setText(user.personal.descriptive.name + " " + user.personal.descriptive.surname);
        this.user = user;
    }

    @Override
    public void answerGetImage(Bitmap bitmap) {
        userImage.setImageBitmap(bitmap);
    }

    @Override
    public void notUsers() {
        Toast.makeText(getContext(), "Вы оценили всех участников группы. Спасибо!", Toast.LENGTH_LONG).show();
        getActivity().onBackPressed();
    }

    private void treatmentAnswers(){
        if (radioGroup1.getCheckedRadioButtonId() == R.id.two1){
            countDiscipline++;
        }

        if (!(radioGroup2.getCheckedRadioButtonId() == R.id.one2) || !(radioGroup2.getCheckedRadioButtonId() == -1)){
            countDiscipline--;
        }

        if(radioGroup3.getCheckedRadioButtonId() == R.id.two3){
            countDiscipline--;
        }

        if(radioGroup4.getCheckedRadioButtonId() == R.id.one4){
            countDiscipline--;
        }else {
            countDiscipline++;
        }

        if (radioGroup5.getCheckedRadioButtonId() == R.id.two5){
            countDiscipline--;
        } else if(radioGroup5.getCheckedRadioButtonId() == R.id.three5){
            countDiscipline++;
        }

        if (radioGroup6.getCheckedRadioButtonId() == 0){
            //////////////////////
        }

        if (radioGroup7.getCheckedRadioButtonId() == R.id.one6){
            countDiscipline++;
        }

        if (radioGroup8.getCheckedRadioButtonId() == R.id.one8){
            countDiscipline--;
        } else{
            countDiscipline++;
        }

        if (radioGroup9.getCheckedRadioButtonId() == R.id.p_one1){
            countProfessionalism++;
        }else if (radioGroup9.getCheckedRadioButtonId() == R.id.p_one3){
            countProfessionalism--;
        }

        if(radioGroup10.getCheckedRadioButtonId() == R.id.p_two1){
            countProfessionalism++;
        }

        if(radioGroup11.getCheckedRadioButtonId() == R.id.p_three1){
            countProfessionalism++;
        }

        if (radioGroup12.getCheckedRadioButtonId() == R.id.p_four1){
            countProfessionalism++;
        }

        if (radioGroup13.getCheckedRadioButtonId() == R.id.p_five1){
            countProfessionalism++;
        }

        countEfficiency = seekBar1.getProgress() + seekBar2.getProgress() + seekBar3.getProgress() + seekBar4.getProgress();
        Log.i("SASDADASD", Integer.toString(countEfficiency));

        if(radioButton1.isChecked()){
            countLoyalty = countLoyalty + 3;
        }

        if(radioButton2.isChecked()){
            countLoyalty = countLoyalty + 2;
        }

        if(radioButton3.isChecked()){
            countLoyalty = countLoyalty + 1;
        }

        if(radioButton4.isChecked()){
            countLoyalty = countLoyalty + 2;
        }

        if(radioButton5.isChecked()){
            countLoyalty = countLoyalty + 2;
        }

        if(radioButton6.isChecked()){
            countLoyalty = countLoyalty + 0;
        }

        if(radioButton7.isChecked()){
            countLoyalty = countLoyalty + 0;
        }

        if(radioButton8.isChecked()){
            countLoyalty = countLoyalty + 0;
        }

        if(radioButton9.isChecked()){
            countLoyalty = countLoyalty + 0;
        }

        if(radioButton10.isChecked()){
            countLoyalty = countLoyalty + 0;
        }

        countAnswers(countDiscipline, countProfessionalism, countEfficiency, countLoyalty);
    }

    private void countAnswers(int discipline, int professionalism, int efficiency, int loyalty){
        AnswersVoting answersVoting = new AnswersVoting();
        answersVoting.event = idRatingEvent;
        answersVoting.subject = user.id;
        answersVoting.estimate.discipline = discipline;
        answersVoting.estimate.efficiency = efficiency;
        answersVoting.estimate.loyalty = loyalty;
        answersVoting.estimate.professionalism = professionalism;
        votingPresenter.setVoitingAnswer(answersVoting);
    }

    private boolean checkAnswers(){
        boolean result = true;
        if((radioGroup1.getCheckedRadioButtonId() == -1)
                || (radioGroup3.getCheckedRadioButtonId() == -1)
                || (radioGroup4.getCheckedRadioButtonId() == -1)
                || (radioGroup5.getCheckedRadioButtonId() == -1)
                || (radioGroup7.getCheckedRadioButtonId() == -1)
                || (radioGroup8.getCheckedRadioButtonId() == -1)
                || (radioGroup9.getCheckedRadioButtonId() == -1)
                || (radioGroup10.getCheckedRadioButtonId() == -1)
                || (radioGroup11.getCheckedRadioButtonId() == -1)
                || (radioGroup12.getCheckedRadioButtonId() == -1)
                || (radioGroup13.getCheckedRadioButtonId() == -1)){
            result = false;
        }
        return result;
    }

}
