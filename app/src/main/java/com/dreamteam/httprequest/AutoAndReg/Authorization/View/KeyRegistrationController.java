package com.dreamteam.httprequest.AutoAndReg.Authorization.View;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.dreamteam.httprequest.AutoAndReg.Authorization.Presenter.AuthorizationPresenter;
import com.dreamteam.httprequest.AutoAndReg.Authorization.Protocols.AuthorizationViewInterface;
import com.dreamteam.httprequest.MainActivity;
import com.dreamteam.httprequest.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class KeyRegistrationController extends Fragment implements AuthorizationViewInterface {

    private EditText keyEditText;
    private Button enterKeyButton;
    private MainActivity activity;
    private RelativeLayout progressBar;

    private AuthorizationPresenter authorizationPresenter;

    public KeyRegistrationController() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_key_registration_controller, container,
                false);
        keyEditText = view.findViewById(R.id.key_edit_text);
        enterKeyButton = view.findViewById(R.id.enter_key_button);
        progressBar = view.findViewById(R.id.progressBarOverlay);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        activity = (MainActivity) getActivity();
        authorizationPresenter = new AuthorizationPresenter(activity, this);
        super.onCreate(savedInstanceState);
    }

    //льправляем введенный ключ на проверку
    @Override
    public void onStart() {
        enterKeyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                keyEditText.getText();
                progressBar.setVisibility(View.VISIBLE);
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

    @Override
    public void error(String title, String description) {
        Toast.makeText(activity, title + "\n" + description, Toast.LENGTH_LONG).show();
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void showNotFound() {

    }
}
