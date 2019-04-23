package com.dreamteam.httprequest.Authorization;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.dreamteam.httprequest.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class AuthorizationController extends Fragment {

    private EditText loginEditText, passwordEditText;
    private Button authorizationButton;


    public AuthorizationController() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_authorization_controller, container, false);
        loginEditText = view.findViewById(R.id.login);
        passwordEditText = view.findViewById(R.id.password);
        authorizationButton = view.findViewById(R.id.authorization_button);
        return view;
    }

}
