package com.dreamteam.httprequest.ObjectList.View;


import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dreamteam.httprequest.GroupList.View.RecyclerItemClickListener;
import com.dreamteam.httprequest.MainActivity;
import com.dreamteam.httprequest.ObjectList.ObjectData;
import com.dreamteam.httprequest.ObjectList.Presenter.ObjectListPresenter;
import com.dreamteam.httprequest.ObjectList.Protocols.ObjectListViewInterface;
import com.dreamteam.httprequest.R;

import com.dreamteam.httprequest.SelectedList.Preseter.SelectListPresenter;
import com.dreamteam.httprequest.SelectedList.View.SelectAdapter;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
@SuppressLint("ValidFragment")
public class ObjectListController extends Fragment implements ObjectListViewInterface {

    private RecyclerView objectRecyclerView;
    MainActivity activity;
    private ObjectListAdapter adapter;
    private ObjectListPresenter objectListPresenter;

    ArrayList<ObjectData> arrayList;
    String type;


    public ObjectListController(ArrayList<ObjectData> arrayList, String type) {
        // Required empty public constructor
        this.arrayList = arrayList;
        this.type = type;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_groups_list, container, false);
        objectRecyclerView = view.findViewById(R.id.groups_recycler_view);
        objectRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        activity = (MainActivity) getActivity();

        adapter = new ObjectListAdapter(arrayList);
        objectListPresenter = new ObjectListPresenter(this, activity);
        activity.setActionBarTitle(type);
    }

    @Override public void onStart() {
        adapter.objectDataArrayList = arrayList;
        objectRecyclerView.setAdapter(adapter);

        objectRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), objectRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                objectListPresenter.openObjectProfile(arrayList.get(position), type);
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));
        getImage(arrayList);
        super.onStart();
    }

    private void getImage(ArrayList<ObjectData> arrayList){
        for(int i = 0; i < arrayList.size(); i++){
            if (arrayList.get(i).imageData == (null) && (arrayList.get(i).image != (null))){
                objectListPresenter.getImage(arrayList.get(i).id, arrayList.get(i).image);
            }
        }
    }

    @Override
    public void redrawAdapter(String groupID, Bitmap bitmap) {
        if (bitmap != null) {
            adapter.changeItem(groupID, bitmap);
        }
    }
}
