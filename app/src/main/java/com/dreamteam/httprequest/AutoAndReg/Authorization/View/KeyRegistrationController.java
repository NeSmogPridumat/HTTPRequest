package com.dreamteam.httprequest.AutoAndReg.Authorization.View;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.dreamteam.httprequest.AutoAndReg.Authorization.Presenter.AuthorizationPresenter;
import com.dreamteam.httprequest.MainActivity;
import com.dreamteam.httprequest.R;

import java.util.Timer;
import java.util.TimerTask;

import okio.Timeout;

/**
 * A simple {@link Fragment} subclass.
 */
public class KeyRegistrationController extends Fragment {

    private EditText keyEditText;
    private Button enterKeyButton;
    private MainActivity activity;

    private AuthorizationPresenter authorizationPresenter;


    public KeyRegistrationController() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_key_registration_controller, container, false);
        keyEditText = view.findViewById(R.id.key_edit_text);
        enterKeyButton = view.findViewById(R.id.enter_key_button);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        activity = (MainActivity) getActivity();
        authorizationPresenter = new AuthorizationPresenter(activity);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        enterKeyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                keyEditText.getText();
                authorizationPresenter.enableUserAuth(keyEditText.getText().toString(), activity.authDataObject);
            }
        });
        super.onStart();
    }

    @Override
    public void onResume() {
        keyEditText.setText("123");
        super.onResume();
    }

}
