package com.dreamteam.httprequest.Group.View;


import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.dreamteam.httprequest.Group.Entity.GroupData.Group;
import com.dreamteam.httprequest.Group.Presenter.GroupPresenter;
import com.dreamteam.httprequest.Group.Protocols.GroupViewInterface;
import com.dreamteam.httprequest.GroupList.Protocols.GroupsViewInterface;
import com.dreamteam.httprequest.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
@SuppressLint("ValidFragment")
public class GroupController extends Fragment implements GroupViewInterface {

    private TextView titleTextView, descriptionTextView;
    private ImageView groupImageView;
    private RadioButton membersRadioButton;

    GroupPresenter groupPresenter = new GroupPresenter(this);
    Group group;
    String groupID;

    @SuppressLint("ValidFragment")
    public GroupController(String id) {
        groupID = id;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_group_controller, container, false);
        titleTextView = view.findViewById(R.id.group_title_text_view);
        descriptionTextView = view.findViewById(R.id.group_description_text_view);
        groupImageView = view.findViewById(R.id.group_image_view);
        membersRadioButton = view.findViewById(R.id.radio_button_members_group);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        groupPresenter.getGroup(groupID);
        super.onCreate(savedInstanceState);

        Log.i("ControllerGROUP", "ONCreate");
    }


    @Override
    public void outputImageView(Bitmap bitmap) {
        groupImageView.setImageBitmap(bitmap);
    }

    @Override
    public void outputGroupView(String title, String description) {
        this.group = group;
        titleTextView.setText(title);
        descriptionTextView.setText(description);
    }

    @Override
    public void error(String error) {

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void outputMembersView(int members) {
        membersRadioButton.setText(Integer.toString(members) + " members");
    }

//    @Override
//    public void onDestroy() {
//        Log.i("LISTLISTLIST", "DESTROOOOOOOOOOY");
//          titleTextView = null;
//          descriptionTextView = null;
//          groupImageView = null;
//
//         groupPresenter = new GroupPresenter(this);
//         group = null;
//         groupID = null;
//
//        super.onDestroy();
//    }
}
