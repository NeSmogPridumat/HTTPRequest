package com.dreamteam.httprequest;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


/**
 * A simple {@link Fragment} subclass.
 */
@SuppressLint("ValidFragment")
public class BlankFragment extends Fragment {

    Drawable drawable;
    ImageView imageView;
    MainActivity activity;


    public BlankFragment(Drawable drawable, MainActivity activity) {
        this.drawable = drawable;
        this.activity = activity;
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_blank, container, false);
        // Inflate the layout for this fragment
        imageView = view.findViewById(R.id.image_view);
        imageView.setImageDrawable(drawable);
//        activity.hideBottomNavigationView(activity.bottomNavigationView);
        activity.bottomNavigationView.setVisibility(View.GONE);
        return view;
    }

    @Override
    public void onStart() {
        setHasOptionsMenu(false);
        super.onStart();
    }

    @Override
    public void onDestroy() {
        activity.bottomNavigationView.setVisibility(View.VISIBLE);
//        activity.showBottomNavigationView(activity.bottomNavigationView);
        super.onDestroy();
    }
}
