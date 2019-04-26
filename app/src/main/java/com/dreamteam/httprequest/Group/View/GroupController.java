package com.dreamteam.httprequest.Group.View;


import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.dreamteam.httprequest.Group.Entity.GroupData.Group;
import com.dreamteam.httprequest.Group.Presenter.GroupPresenter;
import com.dreamteam.httprequest.Group.Protocols.GroupViewInterface;
import com.dreamteam.httprequest.GroupList.Protocols.GroupsViewInterface;
import com.dreamteam.httprequest.MainActivity;
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

    GroupPresenter groupPresenter;
    Group group;
    String groupID;
    private String type;

    MainActivity activity;

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
        activity = (MainActivity) getActivity();
        groupPresenter = new GroupPresenter(this, activity);
        setHasOptionsMenu(true);

        Log.i("ControllerGROUP", "ONCreate");
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onStart() {
        Log.i("ControllerGROUP", "ONStart");
        groupPresenter.getGroup(groupID);

        super.onStart();

        membersRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                groupPresenter.getMembers(groupID);
                membersRadioButton.setChecked(false);
            }
        });
    }

    @Override
    public void outputImageView(Bitmap bitmap) {
        groupImageView.setImageBitmap(bitmap);
    }

    @Override
    public void outputGroupView(Group group) {
        this.group = group;
        titleTextView.setText(group.content.simpleData.title);
        descriptionTextView.setText(group.content.simpleData.description);
        activity.setActionBarTitle(group.content.simpleData.title);

//        ab = activity.getActionBar();
//        ab.setTitle(group.content.simpleData.title);
    }

    @Override
    public void error(String error) {

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void outputMembersView(int members) {
        membersRadioButton.setText(Integer.toString(members) + " members");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.group_profile_controller_menu, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_user_in_group:
                groupPresenter.checkListAddUser();
                break;

            //запрос списка с checkBox
            case R.id.delete_user_in_group:
                groupPresenter.checkListDeleteUser();

                break;

            case R.id.add_subgroup:
                groupPresenter.showAddGroup();
                break;

            case  R.id.delete_group:
                groupPresenter.deleteGroup(group);
                break;

            case R.id.change_admin:
                groupPresenter.addAdmin();
                break;

            case R.id.exit_group:
                groupPresenter.exitGroup();
        }
        return super.onOptionsItemSelected(item);
    }


}
