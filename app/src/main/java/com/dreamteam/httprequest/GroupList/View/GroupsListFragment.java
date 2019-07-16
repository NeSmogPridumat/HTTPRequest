package com.dreamteam.httprequest.GroupList.View;

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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.Toast;

import com.dreamteam.httprequest.Group.Entity.GroupData.Group;
import com.dreamteam.httprequest.GroupList.Presenter.GroupsPresenter;
import com.dreamteam.httprequest.GroupList.Protocols.GroupsViewInterface;
import com.dreamteam.httprequest.MainActivity;
import com.dreamteam.httprequest.R;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class GroupsListFragment extends Fragment implements GroupsViewInterface {

    //TODO: не забудь перенести обработчик клика в проект (class RecyclerItemClickListener и обработка outputGroupsView)

    private RecyclerView groupsRecyclerView;
    private GroupAdapter adapter;
    private MainActivity activity;
    public GroupsPresenter groupsPresenter;
    private RelativeLayout progressBarOverlay;
    private ProgressBar progressBar;
    private View view;
    MenuInflater inflater;
    Menu menu;

    private String userID;
    private boolean deleteOn;
    private ArrayList<Group> groups = new ArrayList<>();

    public GroupsListFragment() {

    }

    public static GroupsListFragment newInstance(String userID){
        Bundle args = new Bundle();
        args.putString("userID", userID);
        GroupsListFragment fragment = new GroupsListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_groups_list, container, false);
        groupsRecyclerView = view.findViewById(R.id.groups_recycler_view);
        groupsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        //groupsRecyclerView.setAdapter(adapter);
        progressBarOverlay = view.findViewById(R.id.progressBarOverlay);
        progressBar = view.findViewById(R.id.progressBar);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        activity = (MainActivity) getActivity();
        groupsPresenter = new GroupsPresenter(this, activity);
        deleteOn = false;
        adapter = new GroupAdapter(groups);
        userID = getArguments().getString("userID");
    }

    @Override
    public void onStart() {
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.progress_rotate);
        activity.setSupportActionBar(activity.toolbar);
        progressBar.startAnimation(animation);
        progressBarOverlay.setVisibility(View.VISIBLE);
        groupsPresenter.getGroups(userID);//здесь ID User'а
        activity.setActionBarTitle("List Group");
        super.onStart();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        this.inflater = inflater;
        this.menu = menu;
        super.onCreateOptionsMenu(menu, inflater);
        if (!deleteOn) {
            inflater.inflate(R.menu.group_list_controller, menu);
            MenuItem search1 = menu.findItem(R.id.app_bar_search);
            final SearchView searchView = (SearchView) search1.getActionView();
            search(searchView);
        } else {
            inflater.inflate(R.menu.delete_select_list_controller, menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {//слушатель на нажатие кнопки edit
        switch (item.getItemId()){
            case R.id.add_item_edit:
                groupsPresenter.showAddGroup();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void search(SearchView searchView) {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
    }

    @Override
    public void outputGroupsView(final ArrayList<Group> groupCollection) {//отправка полученного списка групп на отображение в адаптере
        groups = groupCollection;
        adapter.allGroup = groupCollection;
        adapter.groupCollection = groupCollection;
        groupsRecyclerView.setAdapter(adapter);

        progressBarOverlay.setVisibility(View.GONE);

        groupsRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), groupsRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if(adapter.mFilteredList != null && adapter.mFilteredList.size() != 0) {
                    groupsPresenter.openGroup(adapter.mFilteredList.get(position).id, adapter.mFilteredList.get(position).rules);
                }else{
                    groupsPresenter.openGroup(adapter.allGroup.get(position).id, adapter.allGroup.get(position).rules);
                }
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));
    }

    @Override
    public void error(Throwable t) {
        String title = null;
        String description = null;
        if (t instanceof SocketTimeoutException || t instanceof ConnectException) {
            title = view.getResources().getString(R.string.error_connecting_to_server);
            description = view.getResources().getString(R.string.check_the_connection_to_the_internet);
        }else if (t instanceof NullPointerException){
            title = view.getResources().getString(R.string.object_not_found);
            description = "";
        }
        Toast.makeText(activity, title + "\n" + description, Toast.LENGTH_LONG).show();
        progressBarOverlay.setVisibility(View.GONE);
    }

    public void redrawAdapter(String groupID, Bitmap bitmap){//presenter отправляет bitmap/картинку в этот метод, он отправляет их на отображение в адаптере
        if (bitmap != null) {
            adapter.changeItem(groupID, bitmap);
        }
    }

    public void initAdapter(ArrayList<Group> groups){//инициализация адаптера
//        adapter  = new GroupAdapter(groups);
//        groupsRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onDestroy() {
        groupsPresenter = null;
        groups = null;
        super.onDestroy();
    }
}