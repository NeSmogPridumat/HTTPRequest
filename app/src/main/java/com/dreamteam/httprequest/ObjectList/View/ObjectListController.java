package com.dreamteam.httprequest.ObjectList.View;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dreamteam.httprequest.GroupList.View.RecyclerItemClickListener;
import com.dreamteam.httprequest.MainActivity;
import com.dreamteam.httprequest.ObjectList.ObjectData;
import com.dreamteam.httprequest.ObjectList.Presenter.ObjectListPresenter;
import com.dreamteam.httprequest.ObjectList.Protocols.ObjectListViewInterface;
import com.dreamteam.httprequest.QueryPreferences;
import com.dreamteam.httprequest.R;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Collections;

/**
 * A simple {@link Fragment} subclass.
 */
@SuppressLint("ValidFragment")
public class ObjectListController extends Fragment implements ObjectListViewInterface {

    private RecyclerView objectRecyclerView;
    private MainActivity activity;
    private ObjectListAdapter adapter;
    private ObjectListPresenter objectListPresenter;

    private View view;
    private TextView titleTextView, descriptionTextView;
    private ImageView imageView, adminImageView;

    private ArrayList<ObjectData> arrayList;
    private String type;


    public ObjectListController(ArrayList<ObjectData> arrayList, String type) {
        // Required empty public constructor
        this.arrayList = arrayList;
        this.type = type;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_objects_list, container, false);
        objectRecyclerView = view.findViewById(R.id.groups_recycler_view);
        objectRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


        titleTextView = view.findViewById(R.id.object_list_title_text_view);
        descriptionTextView = view.findViewById(R.id.object_list_description_text_view);
        imageView = view.findViewById(R.id.object_list_image_view);
        this.view = view;
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        activity = (MainActivity) getActivity();

        objectListPresenter = new ObjectListPresenter(this, activity);
        activity.setActionBarTitle(type);
    }

    @Override public void onStart() {

//        if(type.equals("User")) {
//            for (int i = 0; i < arrayList.size(); i++) {
//                if (arrayList.get(i).admin.equals(QueryPreferences.getUserIdPreferences(getContext()))) {
//                    arrayList.add(0, arrayList.get(i));
//                    arrayList.remove(i + 1);
//                }
//            }
//        }
        adapter = new ObjectListAdapter(arrayList);
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

            objectListPresenter.getImage(arrayList.get(i).id, type);

        }
    }

    @Override
    public void redrawAdapter(String groupID, Bitmap bitmap) {
        if (bitmap != null) {
            adapter.changeItem(groupID, bitmap);
        }
    }

    @Override
    public void error(Throwable t) {
        String title = null;
        String description  = null;
        if (t instanceof SocketTimeoutException) {
            title = view.getResources().getString(R.string.error_connecting_to_server);
            description = view.getResources().getString(R.string.check_the_connection_to_the_internet);
        }else if (t instanceof NullPointerException) {
            title = view.getResources().getString(R.string.object_not_found);
            description = "";
        }
        Toast.makeText(activity, title + "\n" + description, Toast.LENGTH_LONG).show();
    }
}
