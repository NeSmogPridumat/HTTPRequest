package com.dreamteam.httprequest.User.View;


import android.annotation.SuppressLint;
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
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.dreamteam.httprequest.MainActivity;
import com.dreamteam.httprequest.R;
import com.dreamteam.httprequest.User.Entity.UserData.User;
import com.dreamteam.httprequest.User.Presenter.PresenterUser;
import com.dreamteam.httprequest.User.Protocols.ViewUserInterface;

/**
 * A simple {@link Fragment} subclass.
 */
@SuppressLint("ValidFragment")
public class UserFragment extends Fragment implements ViewUserInterface {

    ImageView userImage, raitingStoryImage, scheduleImage;
    TextView userName, userSurName, mail, call, rating, groupTitle;
    RadioButton groupsRadioButton;
    MainActivity activity;

    public PresenterUser presenterUser;

    User user = new User();
    Bitmap bitmapU;
    String errors;
    String userID;

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

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        activity = (MainActivity) getActivity();
        presenterUser = new PresenterUser(this, activity);
    }

    @Override
    public void onStart() {
        presenterUser.getUser(userID);
        super.onStart();
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
    }

    @Override
    public void ViewImage(Bitmap bitmap) {
        bitmapU = bitmap;
        userImage.setImageBitmap(bitmapU);
//        user.content.mediaData.bitmap = bitmapU;
    }

    @Override
    public void error(String error) {
        errors = error;
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
//                MainActivity activityAction = (MainActivity) getActivity();
//                activityAction.editProfile(user, bitmapU);//запуск метода Activity
        }
        return super.onOptionsItemSelected(item);
    }
}





