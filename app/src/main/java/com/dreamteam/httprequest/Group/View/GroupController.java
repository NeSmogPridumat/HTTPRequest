package com.dreamteam.httprequest.Group.View;


import android.annotation.SuppressLint;
import android.graphics.Bitmap;
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
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dreamteam.httprequest.Data.AddData;
import com.dreamteam.httprequest.Data.RequestInfo;
import com.dreamteam.httprequest.Group.Entity.GroupData.Group;
import com.dreamteam.httprequest.Group.Presenter.GroupPresenter;
import com.dreamteam.httprequest.Group.Protocols.GroupViewInterface;
import com.dreamteam.httprequest.MainActivity;
import com.dreamteam.httprequest.R;
import com.dreamteam.httprequest.User.Entity.UserData.User;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
@SuppressLint("ValidFragment")
public class GroupController extends Fragment implements GroupViewInterface {

    private TextView titleTextView, descriptionTextView;
    private ImageView groupImageView;
    private RadioButton membersRadioButton, subgroupRadioButton;
    private int rules;
    private Bitmap bitmap;
    private RelativeLayout progressBar;

    private GroupPresenter groupPresenter;
    Group group;
    String groupID;

    MainActivity activity;

    @SuppressLint("ValidFragment")
    public GroupController(String id, int rules) {
        this.rules = rules;
        groupID = id;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_group_controller, container, false);
        titleTextView = view.findViewById(R.id.group_title_text_view);
        descriptionTextView = view.findViewById(R.id.group_description_text_view);
        groupImageView = view.findViewById(R.id.group_image_view);
        progressBar = view.findViewById(R.id.progressBarOverlay);
        membersRadioButton = view.findViewById(R.id.radio_button_members_group);
        subgroupRadioButton = view.findViewById(R.id.radio_button_subgroup);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        activity = (MainActivity) getActivity();
        groupPresenter = new GroupPresenter(this, activity);
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onStart() {
        progressBar.setVisibility(View.VISIBLE);
        groupPresenter.getGroup(groupID);
        super.onStart();

        membersRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                membersRadioButton.setChecked(false);
                groupPresenter.getMembers(groupID);

            }
        });

        subgroupRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                subgroupRadioButton.setChecked(false);
                if (group.nodeData.childList.size() != 0) {
                    progressBar.setVisibility(View.VISIBLE);
                    groupPresenter.getSubgroup(group.nodeData.childList);
                }
            }
        });
    }

    @Override
    public void outputImageView(Bitmap bitmap) {
        this.bitmap = bitmap;
        groupImageView.setImageBitmap(bitmap);
    }

    @Override
    public void outputGroupView(Group group) {
        this.group = group;
        titleTextView.setText(group.content.simpleData.title);
        descriptionTextView.setText(group.content.simpleData.description);
        subgroupRadioButton.setText(group.nodeData.childList.size() + " subgroups");
        activity.setActionBarTitle(group.content.simpleData.title);
        rules = group.rules;
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void error(String title, String description) {
        Toast.makeText(activity, title + "\n" + description, Toast.LENGTH_LONG).show();
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void outputMembersView(ArrayList<User> members) {//TODO возможно стоит передавать не список а int значение
        membersRadioButton.setText(Integer.toString(members.size()) + " members");
    }

    @Override
    public void answerStartVoited() {
        Toast.makeText(getContext(), "Голосование запущено", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (rules == 7) {
            inflater.inflate(R.menu.group_profile_rules7_controller_menu, menu);
        } else if (rules == 1) {
            inflater.inflate(R.menu.group_profile_rules1_controller_menu, menu);
        } else if (rules == 0) {
            inflater.inflate(R.menu.group_profile_rules0_controller_menu, menu);
        }
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
                RequestInfo requestInfo = new RequestInfo();
                requestInfo.creatorID = activity.userID;
                requestInfo.groupCreatorID = groupID;
                requestInfo.groupID = groupID;
                groupPresenter.deleteGroup(requestInfo);
                break;

            case R.id.change_admin:
                groupPresenter.addAdmin();
                break;

            case R.id.exit_group:
                RequestInfo requestInfoExit = new RequestInfo();
                requestInfoExit.creatorID = activity.userID;
                requestInfoExit.groupID = group.id;
                requestInfoExit.userID = activity.userID;
                requestInfoExit.groupCreatorID = group.id;
                groupPresenter.exitGroup(requestInfoExit);
                break;

            case R.id.voited:
                RequestInfo requestInfoVoited = new RequestInfo();
                requestInfoVoited.creatorID = activity.userID;
                requestInfoVoited.groupCreatorID = groupID;
                requestInfoVoited.groupID = groupID;
                groupPresenter.startVoited(requestInfoVoited);
                break;

            case R.id.edit_group:
                RequestInfo requestInfoEdit = new RequestInfo();
                requestInfoEdit.addData = new AddData();
                requestInfoEdit.addData.id = groupID;
                requestInfoEdit.addData.content.simpleData.title = titleTextView.getText().toString();
                requestInfoEdit.addData.content.simpleData.description = descriptionTextView.getText().toString();
                requestInfoEdit.creatorID = activity.userID;
                requestInfoEdit.groupCreatorID = groupID;
                groupPresenter.showEditGroup(requestInfoEdit, bitmap);
        }
        return super.onOptionsItemSelected(item);
    }
}
