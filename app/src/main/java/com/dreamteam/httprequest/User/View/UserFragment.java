package com.dreamteam.httprequest.User.View;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

import com.dreamteam.httprequest.Data.QuestionRating.Question;
import com.dreamteam.httprequest.Data.QuestionRating.QuestionRating;
import com.dreamteam.httprequest.MainActivity;
import com.dreamteam.httprequest.QueryPreferences;
import com.dreamteam.httprequest.R;
import com.dreamteam.httprequest.Service.EventService;
import com.dreamteam.httprequest.User.Entity.UserData.User;
import com.dreamteam.httprequest.User.Presenter.PresenterUser;
import com.dreamteam.httprequest.User.Protocols.ViewUserInterface;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserFragment extends Fragment implements ViewUserInterface {

    private ImageView userImage;
    private TextView userName, userSurName, mail, call, rating;
    private RelativeLayout progressBarOverlay;
    private ProgressBar progressBar, ratingBar;
    private MainActivity activity;
    private LinearLayout userLinear;
    private View view;

    public PresenterUser presenterUser;

    private User user = new User();
    private Bitmap bitmapU;
    private String userID;
    private boolean count = false;

    public UserFragment() {
    }

    public static UserFragment newInstance(String userID){
        Bundle args = new Bundle();
        args.putString("userID", userID);
        UserFragment fragment = new UserFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_user, container, false);
        userName = view.findViewById(R.id.user_name_text_view);
        userSurName = view.findViewById(R.id.user_surname_text_view);
        mail = view.findViewById(R.id.description_profile_text_view);
        call = view.findViewById(R.id.call_number_text_view);
        userImage = view.findViewById(R.id.user_image);
        progressBarOverlay = view.findViewById(R.id.progressBarOverlay);
        progressBar = view.findViewById(R.id.progressBar);
        userLinear = view.findViewById(R.id.user_fragment);
        ratingBar = view.findViewById(R.id.progressBar2);
        rating = view.findViewById(R.id.textView2);
        if(!count){
            count = true;
            progressBarOverlay.setVisibility(View.VISIBLE);
        }

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

        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.progress_rotate);
        progressBar.startAnimation(animation);
        presenterUser.getUser(userID);
        super.onStart();

        presenterUser.getRating(userID);

        userImage.setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View v) {
            if(userImage.getDrawable() != null){
                //TODO сделать через презентер, либо другой способ отображения!!!
                activity.showImage(userImage.getDrawable());
            }
            }
        });

        activity.setSupportActionBar(activity.toolbar);
        rating.setText("N");
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.user_fragment, menu);
    }

    @Override
    public void View(User user) {
        userName.setText(user.content.simpleData.name);
        userSurName.setText(user.content.simpleData.surname);
        this.user = user;
        activity.setActionBarTitle("Профиль");
        progressBarOverlay.setVisibility(View.GONE);
    }

    @Override
    public void ViewImage(Bitmap bitmap) {
        bitmapU = bitmap;
        userImage.setImageBitmap(bitmapU);
    }

    @Override
    public void error(Throwable t) {
        String title = null;
        String description = null;
        if (t instanceof SocketTimeoutException || t instanceof ConnectException) {
            title = view.getResources().getString(R.string.error_connecting_to_server);
            description = view.getResources().getString(R.string.check_the_connection_to_the_internet);
        }else if (t instanceof NullPointerException) {
            title = view.getResources().getString(R.string.object_not_found);
            description = "";
        }
        Toast.makeText(activity, title + "\n" + description, Toast.LENGTH_LONG).show();
        progressBarOverlay.setVisibility(View.GONE);
    }

    @Override
    public void answerGetRating(ArrayList<QuestionRating> questionRatings) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1f);
        layoutParams.setMargins(24,30,0,0);
        LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1f);
        DecimalFormat df = new DecimalFormat("#.##");
        layoutParams1.setMarginStart(48);
        float j = 0;
        for (Question i : questionRatings.get(0).question){//TODO: вроде понятный ForEach
            j = j + i.middleValue;
        }

        //TODO: возможно надо убрать в метод
        TextView textView = new TextView(view.getContext());
        String text =view.getResources().getText(R.string.overall_rating) + " "
                + df.format(j/questionRatings.get(0).question.size());
        int overRating = (int)((j/questionRatings.get(0).question.size()/3)*100);
        ratingBar.setProgress(overRating);
        rating.setText(ratingChar(overRating));

        textView.setText(text);
        textView.setTextSize(18f);
        if(getContext() != null) {
            textView.setTextColor(getResources().getColor(android.R.color.black));
        }//TODO: возможно надо обернуть в getContext() != null
        textView.setLayoutParams(layoutParams);
        userLinear.addView(textView);
        for (Question i : questionRatings.get(0).question) {
            userLinear.addView(createTextView(i, df, layoutParams1));
        }
    }

    private String ratingChar (int overRating){
        String rating;
        if (overRating >= 90){
            rating = "A+";
        }else if (overRating >= 80){
            rating = "A";
        } else if (overRating >= 70) {
            rating = "B+";
        }else if (overRating >= 60){
            rating = "B";
        } else if (overRating >= 50){
            rating = "C+";
        }else if (overRating >= 40){
            rating = "C";
        } else if (overRating >= 30) {
            rating = "E+";
        } else rating = "E";

        return rating;
    }

    private TextView createTextView(Question question, DecimalFormat df, LinearLayout.LayoutParams layoutParams){
        TextView textView = new TextView(view.getContext());
        textView.setTextColor(view.getResources().getColor(android.R.color.black));
        textView.setTextSize(18f);
        String text = question.title + ": "
                + df.format(question.middleValue);
        textView.setText(text);
        textView.setLayoutParams(layoutParams);
        return textView;
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





