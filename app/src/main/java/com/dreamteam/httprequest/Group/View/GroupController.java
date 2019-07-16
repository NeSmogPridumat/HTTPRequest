package com.dreamteam.httprequest.Group.View;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dreamteam.httprequest.Data.AddData;
import com.dreamteam.httprequest.Data.RequestInfo;
import com.dreamteam.httprequest.Event.Entity.EventType4.EventType4;
import com.dreamteam.httprequest.Group.Entity.GroupData.Group;
import com.dreamteam.httprequest.Group.Presenter.GroupPresenter;
import com.dreamteam.httprequest.Group.Protocols.GroupViewInterface;
import com.dreamteam.httprequest.MainActivity;
import com.dreamteam.httprequest.R;
import com.dreamteam.httprequest.User.Entity.UserData.User;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Collections;

/**
 * A simple {@link Fragment} subclass.
 */
public class GroupController extends Fragment implements GroupViewInterface {

    private TextView titleTextView, descriptionTextView;
    private ImageView groupImageView;
    private int rules;
    private Button usersButton, subgroupButton;
    private Bitmap bitmap;
    private RelativeLayout progressBar;
    private RecyclerView groupEventRecycler;
    private View view;

    private GroupPresenter groupPresenter;
    Group group;
    String groupID;

    MainActivity activity;

    public GroupController() {
    }

    public static GroupController newInstance(String groupID, int rules){
        Bundle args = new Bundle();
        args.putString("groupID", groupID);
        args.putInt("rules", rules);
        GroupController fragment = new GroupController();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_group_controller, container, false);
        titleTextView = view.findViewById(R.id.group_title_text_view);
        descriptionTextView = view.findViewById(R.id.group_description_text_view);
        groupImageView = view.findViewById(R.id.group_image_view);
        progressBar = view.findViewById(R.id.progressBarOverlay);
        usersButton = view.findViewById(R.id.button_users_group);
        subgroupButton = view.findViewById(R.id.button_subgroup);
        groupEventRecycler = view.findViewById(R.id.group_event_list);
        groupEventRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        groupID = getArguments().getString("groupID");
        rules = getArguments().getInt("rules");
        activity = (MainActivity) getActivity();
        groupPresenter = new GroupPresenter(this, activity);

        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        progressBar.setVisibility(View.VISIBLE);
        groupPresenter.getGroup(groupID);
        groupPresenter.getEventForGroup(groupID);
        super.onStart();

        usersButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                groupPresenter.getMembers(groupID);

            }
        });

        subgroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(group.nodeData.childList.size() == 0){
                    subgroupButton.setClickable(false);
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    groupPresenter.getSubgroup(group.nodeData.childList);
                }
            }
        });

        groupImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(groupImageView.getDrawable() != null){
                    //TODO сделать через презентер, либо другой способ отображения!!!
                    activity.showImage(groupImageView.getDrawable());
                }
            }
        });

        activity.setSupportActionBar(activity.toolbar);

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
        if (getContext() != null) {
            subgroupButton.setText(Integer.toString(group.nodeData.childList.size()));
        }
        activity.setActionBarTitle(group.content.simpleData.title);
        rules = group.rules;
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void error(Throwable t) {
        String title = null;
        String description  = null;
        if (t instanceof SocketTimeoutException) {
            title = view.getResources().getString(R.string.error_connecting_to_server);
            description = view.getResources()
                    .getString(R.string.check_the_connection_to_the_internet);
        }else if (t instanceof NullPointerException) {
            title = view.getResources().getString(R.string.object_not_found);
            description = "";
        }
        Toast.makeText(activity, title + "\n" + description, Toast.LENGTH_LONG).show();
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void errorHanding(String title, String description) {
        Toast.makeText(activity, title + "\n" + description, Toast.LENGTH_LONG).show();
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void answerEventForGroup(ArrayList<EventType4> eventList) {
        Collections.reverse(eventList);
        EventForGroupAdapter adapter = new EventForGroupAdapter(eventList);
        groupEventRecycler.setAdapter(adapter);
        groupEventRecycler.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void outputMembersView(ArrayList<User> members) {
        if (getContext() != null) {
            usersButton.setText(Integer.toString(members.size()));
        }
        int count = 0;
        for (int i = 0; i < members.size(); i++){
            if (activity.userID.equals(members.get(i).id)){
                count = count + 1;
            }
        }
        if (count == 0){
            setHasOptionsMenu(false);
        } else {
            setHasOptionsMenu(true);
        }
    }

    @Override
    public void answerStartVoited() {
        Toast.makeText(getContext(), getResources().getText(R.string.voting_started), Toast.LENGTH_LONG).show();
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
