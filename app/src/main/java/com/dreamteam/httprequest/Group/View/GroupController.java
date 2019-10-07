package com.dreamteam.httprequest.Group.View;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.dreamteam.httprequest.Data.AddData;
import com.dreamteam.httprequest.Data.RequestInfo;
import com.dreamteam.httprequest.Event.Entity.Events.EventsKinds.DataEvents.Event;
import com.dreamteam.httprequest.Event.Entity.Events.EventsObject;
import com.dreamteam.httprequest.Group.Entity.GroupData.EditGroupData.EditGroupData;
import com.dreamteam.httprequest.Group.Entity.GroupData.Group;
import com.dreamteam.httprequest.Group.Presenter.GroupPresenter;
import com.dreamteam.httprequest.Group.Protocols.GroupViewInterface;
import com.dreamteam.httprequest.MainActivity;
import com.dreamteam.httprequest.QueryPreferences;
import com.dreamteam.httprequest.R;
import com.dreamteam.httprequest.User.Entity.UserData.User;

import java.net.SocketTimeoutException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * A simple {@link Fragment} subclass.
 */
public class GroupController extends Fragment implements GroupViewInterface {

    private TextView titleTextView, descriptionTextView;
    private ImageView groupImageView;
//    private int rules;
    private Button usersButton, subgroupButton;
    private Bitmap bitmap;
    private RelativeLayout progressBar;
    private View view;
    private LinearLayout forEventLinearLayout, forRatingLinearLayout;

    private GroupPresenter groupPresenter;
    private Group group;
    private String groupID;

    private MainActivity activity;
    public GroupController() {
    }

    public static GroupController newInstance(String groupID){
        Bundle args = new Bundle();
        args.putString("groupID", groupID);
       // args.putInt("rules", rules);
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
        forEventLinearLayout = view.findViewById(R.id.for_event_linear_layout);
        forRatingLinearLayout = view.findViewById(R.id.for_rating_linear_layout);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        groupID = getArguments().getString("groupID");
        //rules = getArguments().getInt("rules");
        activity = (MainActivity) getActivity();
        groupPresenter = new GroupPresenter(this, activity);

        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        progressBar.setVisibility(View.VISIBLE);
        groupPresenter.getGroup(groupID);
        super.onStart();

        usersButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                groupPresenter.getMembers(group.members);

            }
        });

        subgroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(group.nodeData.children.size() == 0){
                    subgroupButton.setClickable(false);
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    groupPresenter.getSubgroup(group.nodeData.children);
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
        forEventLinearLayout.removeAllViews();
        groupPresenter.getEvents();

        activity.setSupportActionBar(activity.toolbar);
    }

    @Override
    public void outputImageView(Bitmap bitmap) {
        this.bitmap = bitmap;
        groupImageView.setImageBitmap(bitmap);
    }

    @Override
    public void outputGroupView(final Group group) {
        this.group = group;
        titleTextView.setText(group.personal.descriptive.title);
        descriptionTextView.setText(group.personal.descriptive.description);
        if(group.status.equals("confirmed")){
            setHasOptionsMenu(true);
        } else {
            descriptionTextView.setText(("Не подтверждена"));
        }
        subgroupButton.setText(Integer.toString(group.nodeData.children.size()));
        subgroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                groupPresenter.getSubgroup(group.nodeData.children);
            }
        });

        usersButton.setText(Integer.toString(group.members.size()));
        activity.setActionBarTitle(group.personal.descriptive.title);
        //rules = group.rules;
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
    public void answerGetEvents(EventsObject eventObject) {
        ArrayList<Event> discussions = eventObject.events.discussions;

        if (eventObject.events.ratings.size() != 0){
            visibleRatingLinearLayout(eventObject.events.ratings);
        }

        if(discussions.size() > 1) {
            Collections.sort(discussions, new Comparator<Event>() {
                @Override
                public int compare(Event o1, Event o2) {
                    return Long.compare(o1.closingTime, o2.closingTime);
                }
            });
        }

        for(int i = 0; i < eventObject.events.discussions.size(); i++) {
            final View view = getLayoutInflater().inflate(R.layout.discussion_view, forEventLinearLayout, false);
            if (view.getParent() != null) {
                ((ViewGroup) view.getParent()).removeView(view); // <- fix
            }
            TextView titleTextView = view.findViewById(R.id.titleTextView);
            TextView messageTextView = view.findViewById(R.id.message);

            titleTextView.setText(eventObject.events.discussions.get(i).data.title);
            messageTextView.setText(eventObject.events.discussions.get(i).data.text);

            Date d = new Date(eventObject.events.discussions.get(i).closingTime);
            TextView textViewDate = view.findViewById(R.id.date);
            SimpleDateFormat formatForDate = new SimpleDateFormat(" yyyy.MM.dd hh:mm:ss");
            textViewDate.setText("Время завершения: " + formatForDate.format(d));

            if (eventObject.events.discussions.get(i).data.priority.equals("info")){
                titleTextView.setBackgroundColor(view.getResources().getColor(R.color.colorDarkBlue));
            } else if (eventObject.events.discussions.get(i).data.priority.equals("warning")){
                titleTextView.setBackgroundColor(view.getResources().getColor(R.color.yellow));
            }else if (eventObject.events.discussions.get(i).data.priority.equals("danger")){
                titleTextView.setBackgroundColor(view.getResources().getColor(android.R.color.holo_red_dark));
            }
            forEventLinearLayout.addView(view);
        }
    }

    private void  visibleRatingLinearLayout(final ArrayList<Event> ratings){
        for (String i : group.members){
            if (i.equals(QueryPreferences.getUserIdPreferences(getContext()))){
                forRatingLinearLayout.setVisibility(View.VISIBLE);
                forRatingLinearLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        groupPresenter.openVoting(ratings.get(0).id);
                    }
                });
            }
        }
    }

    @Override
    public void answerStartDiscussion() {
        forEventLinearLayout.removeAllViews();
        groupPresenter.getEvents();
    }

    @Override
    public void answerGetUsersForSelectAdd(ArrayList<User> users) {
        ArrayList<User> removeUser = new ArrayList<>();
        for (User user: users){
            for (String i : group.members){
                if(user.id.equals(i)){
                    removeUser.add(user);
                }
            }
        }
        for (User user: removeUser){
            users.remove(user);
        }
        groupPresenter.setUserSelect(users);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
    }

//    @Override
//    public void outputMembersView(ArrayList<User> members) {
//        if (getContext() != null) {
//            usersButton.setText(Integer.toString(members.size()));
//        }
//        int count = 0;
//        for (int i = 0; i < members.size(); i++){
//            if (activity.userID.equals(members.get(i).id)){
//                count = count + 1;
//            }
//        }
//        if (count == 0){
//            setHasOptionsMenu(false);
//        } else {

//        }
//    }

    @Override
    public void answerStartVoited() {
        Toast.makeText(getContext(), getResources().getText(R.string.voting_started), Toast.LENGTH_LONG).show();
        groupPresenter.getEvents();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (group.admin.equals(QueryPreferences.getUserIdPreferences(getContext()))) {
            inflater.inflate(R.menu.group_profile_rules7_controller_menu, menu);
        } else {
            inflater.inflate(R.menu.group_profile_rules1_controller_menu, menu);
//        } else if (rules == 0) {
//            inflater.inflate(R.menu.group_profile_rules0_controller_menu, menu);
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
                groupPresenter.checkListDeleteUser(group.members);
                break;

            case R.id.add_subgroup:
                groupPresenter.showAddGroup();
                break;

            case  R.id.delete_group:
//                RequestInfo requestInfo = new RequestInfo();
//                requestInfo.creatorID = activity.userID;
//                requestInfo.groupCreatorID = groupID;
//                requestInfo.groupID = groupID;
                groupPresenter.deleteGroup(group.id);
                break;

            case R.id.change_admin:
                groupPresenter.addAdmin(group.members);
                break;

            case R.id.exit_group:
//                RequestInfo requestInfoExit = new RequestInfo();
//                requestInfoExit.creatorID = activity.userID;
//                requestInfoExit.groupID = group.id;
//                requestInfoExit.userID = activity.userID;
//                requestInfoExit.groupCreatorID = group.id;

                EditGroupData editGroupData = new EditGroupData();
                editGroupData.members = new ArrayList<>();
                editGroupData.members.add(QueryPreferences.getUserIdPreferences(getContext()));
                groupPresenter.exitGroup(editGroupData);
                break;

            case R.id.voited:
                groupPresenter.startVoited(group.id);
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
                break;

            case R.id.discussion:
                groupPresenter.startCreateDiscussionFragment(group.id);
                break;

        }
        return super.onOptionsItemSelected(item);
    }
}
