package com.dreamteam.httprequest.AutoAndReg.Authorization.View;


import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.dreamteam.httprequest.AutoAndReg.Authorization.AuthorizationRouter;
import com.dreamteam.httprequest.AutoAndReg.Authorization.Presenter.AuthorizationPresenter;
import com.dreamteam.httprequest.AutoAndReg.Authorization.Protocols.AuthorizationViewInterface;
import com.dreamteam.httprequest.MainActivity;
import com.dreamteam.httprequest.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class RegistrationController extends Fragment implements AuthorizationViewInterface {

    private MainActivity activity;
    private AuthorizationPresenter authorizationPresenter;
    private RelativeLayout progressBar;


    public RegistrationController() {
        // Required empty public constructor
    }

    private EditText loginEditText, passwordEditText;
    private Button authorizationButton, registrationButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_registration_controller, container, false);
        loginEditText = view.findViewById(R.id.registration_login);
        passwordEditText = view.findViewById(R.id.registration_password);
        registrationButton = view.findViewById(R.id.registration_button);
        progressBar = view.findViewById(R.id.progressBarOverlay);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        activity = (MainActivity) getActivity();
        authorizationPresenter = new AuthorizationPresenter(activity, this);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        //слушатель на кнопку, проверяет заполнены ли логин и пароль и отправляет данные на сервер
        registrationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((loginEditText.getText().toString().equals("")))//-----------------------------------проверка на заполнение поля
                {
                    loginEditText.setError("Заполните поле");
                    loginEditText.requestFocus();
                } else if (passwordEditText.getText().toString().equals("")){
                    passwordEditText.setError("Заполниет поле");
                    passwordEditText.requestFocus();
                } else {//---------------------------------------------------------------------------------код отправки изменеий на сервер
                    String login = loginEditText.getText().toString();
                    String password = passwordEditText.getText().toString();
                    progressBar.setVisibility(View.VISIBLE);

                    //отправка данных на изменение
                    authorizationPresenter.createLogin(login, password);
                }
            }
        });
        super.onStart();
    }

    @Override
    public void error(String title, String description) {
        Toast.makeText(activity, title + "\n" + description, Toast.LENGTH_LONG).show();
        progressBar.setVisibility(View.GONE);

    }
}
