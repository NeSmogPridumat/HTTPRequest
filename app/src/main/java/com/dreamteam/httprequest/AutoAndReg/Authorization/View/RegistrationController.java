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

import java.net.SocketTimeoutException;

/**
 * A simple {@link Fragment} subclass.
 */
public class RegistrationController extends Fragment implements AuthorizationViewInterface {

    private MainActivity activity;
    private AuthorizationPresenter authorizationPresenter;
    private RelativeLayout progressBar;

    private EditText loginEditText, passwordEditText;
    private Button registrationButton;

    public RegistrationController() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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
                if ((loginEditText.getText().toString().isEmpty()))//-----------------------------------проверка на заполнение поля
                {
                    loginEditText.setError(getResources().getString(R.string.fill_in_the_field));
                    loginEditText.requestFocus();
                } else if (passwordEditText.getText().toString().isEmpty()){
                    passwordEditText.setError(getResources().getString(R.string.fill_in_the_field));
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
    public void error(Throwable t) {
        String title = null;
        String description  = null;
        if (t instanceof SocketTimeoutException) {
            title = getResources().getString(R.string.error_connecting_to_server);
            description = getResources()
                    .getString(R.string.check_the_connection_to_the_internet);
        }else if (t instanceof NullPointerException) {
            title = getResources().getString(R.string.object_not_found);
            description = "";
        }
        Toast.makeText(activity, title + "\n" + description, Toast.LENGTH_LONG).show();
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void showNotFound() {

    }
}
