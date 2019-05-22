package com.dreamteam.httprequest.User.View;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dreamteam.httprequest.MainActivity;
import com.dreamteam.httprequest.R;
import com.dreamteam.httprequest.Service.EventService;
import com.dreamteam.httprequest.User.Entity.UserData.User;
import com.dreamteam.httprequest.User.Presenter.PresenterUser;
import com.dreamteam.httprequest.User.Protocols.ViewUserInterface;

/**
 * A simple {@link Fragment} subclass.
 */
@SuppressLint("ValidFragment")
public class UserFragment extends Fragment implements ViewUserInterface {

    private ImageView userImage, raitingStoryImage, scheduleImage;
    private TextView userName, userSurName, mail, call, rating, groupTitle;
    private RelativeLayout progressBar;
    private RadioButton groupsRadioButton;
    private MainActivity activity;

    public PresenterUser presenterUser;

    private User user = new User();
    private Bitmap bitmapU;
    private String userID;

    public UserFragment(String userID) {
        this.userID = userID;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user, container, false);
        userName = view.findViewById(R.id.user_name_text_view);
        userSurName = view.findViewById(R.id.user_surname_text_view);
        mail = view.findViewById(R.id.mail_text_view);
        call = view.findViewById(R.id.call_number_text_view);
        rating = view.findViewById(R.id.rating_text_view);
        groupTitle = view.findViewById(R.id.group_title);
        userImage = view.findViewById(R.id.user_image);
        groupsRadioButton = view.findViewById(R.id.radio_button_groups);
        progressBar = view.findViewById(R.id.progressBarOverlay);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = (MainActivity) getActivity();
        if (userID.equals(activity.userID)) {
            setHasOptionsMenu(true);
        }
        presenterUser = new PresenterUser(this, activity);
    }

    @Override
    public void onStart() {
        progressBar.setVisibility(View.VISIBLE);
        presenterUser.getUser(userID);
        super.onStart();
        groupsRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                if (activity.userID.equals(userID)) {
                   activity.bottomNavigationView.setSelectedItemId(R.id.groups);
                }else {
                    presenterUser.getGroups(userID);
                }
                groupsRadioButton.setChecked(false);
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
        userName.setText(user.content.simpleData.name);
        userSurName.setText(user.content.simpleData.surname);
        this.user = user;
        activity.setActionBarTitle(user.content.simpleData.name);
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void ViewImage(Bitmap bitmap) {
        bitmapU = bitmap;
        userImage.setImageBitmap(bitmapU);
    }

    @Override
    public void error(String title, String description) {
        Toast.makeText(activity, title + "\n" + description, Toast.LENGTH_LONG).show();
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void answerGetGroups(int groups) {
        groupsRadioButton.setText((Integer.toString(groups) + " groups"));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {//слушатель на нажатие кнопки edit
        switch (item.getItemId()){
            case R.id.menu_item_edit:
                presenterUser.showEditProfile(user, bitmapU);
                break;

            case R.id.exit_menu_item_edit:
                activity.stopService(new Intent(activity, EventService.class));
                activity.exitLogin();//TODO
        }
        return super.onOptionsItemSelected(item);
    }
}





