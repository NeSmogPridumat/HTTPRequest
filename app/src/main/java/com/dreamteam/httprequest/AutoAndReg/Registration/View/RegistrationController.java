package com.dreamteam.httprequest.AutoAndReg.Registration.View;


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
public class RegistrationController extends Fragment {




    public RegistrationController() {
        // Required empty public constructor
    }

    private EditText loginEditText, passwordEditText;
    private Button authorizationButton, registrationButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_authorization_controller, container, false);
        loginEditText = view.findViewById(R.id.registration_login);
        passwordEditText = view.findViewById(R.id.registration_password);
        registrationButton = view.findViewById(R.id.registration_button);
        return view;
    }

}
