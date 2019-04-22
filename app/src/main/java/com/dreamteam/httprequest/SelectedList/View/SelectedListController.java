package com.dreamteam.httprequest.SelectedList;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dreamteam.httprequest.Group.Entity.GroupData.Group;
import com.dreamteam.httprequest.R;
import com.dreamteam.httprequest.User.User;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
@SuppressLint("ValidFragment")
public class SelectedListController extends Fragment {

    ArrayList<User> listUser = new ArrayList<>();
    ArrayList<Group> listGroup = new ArrayList<>();
    final String USER = "User";
    final String GROUP = "Group";


    public SelectedListController(ArrayList<Object> arrayList, String type) {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_selected_list_controller, container, false);
    }

}
