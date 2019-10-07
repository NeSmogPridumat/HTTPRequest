package com.dreamteam.httprequest;


import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.dreamteam.httprequest.Data.QuestionRating.QuestionRating;
import com.dreamteam.httprequest.Service.EventService;
import com.dreamteam.httprequest.User.Entity.UserData.RatingData.RatingData;
import com.dreamteam.httprequest.User.Entity.UserData.User;
import com.dreamteam.httprequest.User.Presenter.PresenterUser;
import com.dreamteam.httprequest.User.Protocols.ViewUserInterface;
import com.dreamteam.httprequest.User.View.UserFragment;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.text.DecimalFormat;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChuvakEtoRepchik extends Fragment implements ViewUserInterface {

    private ImageView app_bar_image;
//    private TextView userName, userSurName, mail, call;
//    private RelativeLayout progressBarOverlay;
//    private ProgressBar progressBar;
//    private RadioButton groupsRadioButton;
    private MainActivity activity;
    private LinearLayout userLinear;
    private Toolbar toolbar;

    public PresenterUser presenterUser;

    private User user = new User();
    private Bitmap bitmapU;
    private String userID;
    private boolean count = false;

    public ChuvakEtoRepchik() {
    }

    public static ChuvakEtoRepchik newInstance(String userID){
        Bundle args = new Bundle();
        args.putString("userID", userID);
        ChuvakEtoRepchik fragment = new ChuvakEtoRepchik();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chuvak_eto_repchik, container, false);
//        userSurName = view.findViewById(R.id.user_surname_text_view);
//        mail = view.findViewById(R.id.mail_text_view);
//        call = view.findViewById(R.id.call_number_text_view);
        app_bar_image = view.findViewById(R.id.app_bar_image);
//        groupsRadioButton = view.findViewById(R.id.radio_button_groups);
//        progressBarOverlay = view.findViewById(R.id.progressBarOverlay);
//        progressBar = view.findViewById(R.id.progressBar);
        userLinear = view.findViewById(R.id.user_fragment);
        toolbar = view.findViewById(R.id.toolbar);
//        if(!count){
//            count = true;
//            progressBarOverlay.setVisibility(View.VISIBLE);
//        }

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        userID = getArguments().getString("userID");
        activity = (MainActivity) getActivity();
        if (userID.equals(QueryPreferences.getUserIdPreferences(getContext()))) {
            setHasOptionsMenu(true);
        }
        presenterUser = new PresenterUser(this, activity);
        presenterUser.getRating(userID);


        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.progress_rotate);
//        progressBar.startAnimation(animation);
        presenterUser.getUser(userID);
        super.onStart();
//        groupsRadioButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (QueryPreferences.getUserIdPreferences(getContext()).equals(userID)) {
//                    activity.bottomNavigationView.setSelectedItemId(R.id.groups);
//                } else {
//                    presenterUser.getGroups(userID);
//                }
//                groupsRadioButton.setChecked(false);
//            }
//        });

        app_bar_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(app_bar_image.getDrawable() != null){
                    //TODO сделать через презентер, либо другой способ отображения!!!
                    activity.showImage(app_bar_image.getDrawable());
                }
            }
        });
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.user_fragment, menu);
    }

    @Override
    public void View(User user) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar.setTitle(user.personal.descriptive.name + " ");
        }
//        userSurName.setText(user.content.simpleData.surname);
        this.user = user;
        activity.setActionBarTitle(user.personal.descriptive.name + " "  + user.personal.descriptive.surname);
//        activity.getActionBar().hide();
//        progressBarOverlay.setVisibility(View.GONE);
    }

    @Override
    public void ViewImage(Bitmap bitmap) {
        bitmapU = bitmap;
        app_bar_image.setImageBitmap(bitmapU);
    }

    @Override
    public void error(Throwable t) {
        String title = null;
        String description = null;
        if (t instanceof SocketTimeoutException || t instanceof ConnectException) {
            title = getResources().getString(R.string.error_connecting_to_server);
            description = getResources().getString(R.string.check_the_connection_to_the_internet);
        }else if (t instanceof NullPointerException) {
            title = getResources().getString(R.string.object_not_found);
            description = "";
        }
        Toast.makeText(activity, title + "\n" + description, Toast.LENGTH_LONG).show();
//        progressBarOverlay.setVisibility(View.GONE);
    }

    @Override
    public void answerGetRating(RatingData questionRatings) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1f);
        layoutParams.setMargins(24,30,0,0);
        LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1f);
        DecimalFormat df= new DecimalFormat("#.##");
        layoutParams1.setMarginStart(48);
        float j = 0;
//        for (int i = 0; i < questionRatings.get(0).question.size(); i++){
//            j = j + questionRatings.get(0).question.get(i).middleValue;
//        }
//        TextView textView = new TextView(getContext());
//        String text = getResources().getText(R.string.overall_rating) + df.format(j/questionRatings.get(0).question.size());
//        textView.setText(text);
//        textView.setTextSize(36f);
//        textView.setTextColor(getResources().getColor(android.R.color.white));
//        textView.setLayoutParams(layoutParams);
//        userLinear.addView(textView);
//        for (int i = 0; i < questionRatings.get(0).question.size(); i++) {
//            TextView textView1 = new TextView(getContext());
//            textView1.setTextColor(getResources().getColor(android.R.color.white));
//            textView.setTextSize(18f);
//            text = questionRatings.get(0).question.get(i).title + ": "
//                    + df.format(questionRatings.get(0).question.get(i).middleValue);
//            textView1.setText(text);
//            textView1.setLayoutParams(layoutParams1);
//            userLinear.addView(textView1);
//        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {//слушатель на нажатие кнопки edit
        switch (item.getItemId()){
            case R.id.menu_item_edit:
                presenterUser.showEditProfile(user, bitmapU);
                break;

            case R.id.exit_menu_item_edit:
                activity.stopService(new Intent(activity, EventService.class));
                activity.exitLogin();
        }
        return super.onOptionsItemSelected(item);
    }
}