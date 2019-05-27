package com.dreamteam.httprequest.AutoAndReg.Authorization.View;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
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
import android.widget.TextView;
import android.widget.Toast;

import com.dreamteam.httprequest.AutoAndReg.Authorization.Entity.AuthData;
import com.dreamteam.httprequest.AutoAndReg.Authorization.Presenter.AuthorizationPresenter;
import com.dreamteam.httprequest.AutoAndReg.Authorization.Protocols.AuthorizationViewInterface;
import com.dreamteam.httprequest.MainActivity;
import com.dreamteam.httprequest.R;
import com.dreamteam.httprequest.Service.EventService;

import java.net.SocketTimeoutException;


//create new login
//enable user auth
//user autoriz

/**
 * A simple {@link Fragment} subclass.
 */
@SuppressLint("ValidFragment")
public class AuthorizationController extends Fragment implements AuthorizationViewInterface {

    private EditText loginEditText, passwordEditText;
    private Button authorizationButton;
    private TextView registrationTextView;

     private AuthorizationPresenter authorizationPresenter;
     private MainActivity activity;
     private RelativeLayout progressBar;

    public AuthorizationController(MainActivity activity) {
        // Required empty public constructor
        this.activity = activity;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_authorization_controller, container, false);
        loginEditText = view.findViewById(R.id.login);
        passwordEditText = view.findViewById(R.id.password);
        authorizationButton = view.findViewById(R.id.authorization_button);
        registrationTextView = view.findViewById(R.id.authorization_registration_text_view);
        progressBar = view.findViewById(R.id.progressBarOverlay);
        activity.bottomNavigationTextView.setText("");
        stopService();
        return view;
    }

    //остановка работы сервиса
    private void stopService(){
        Intent intent = new Intent(activity, EventService.class);
        activity.stopService(intent);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        authorizationPresenter = new AuthorizationPresenter(activity, this);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        activity.hideBottomNavigationView(activity.bottomNavigationView);

        //кнопка для регистрации, перекидывает на фрагмент для регистрации
        registrationTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                authorizationPresenter.getRegistration();
            }
        });

        authorizationButton.setOnClickListener(new View.OnClickListener() {
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
                    MainActivity activity = (MainActivity) getActivity();
                    activity.authDataObject.authData = new AuthData();
                    activity.authDataObject.authData.login = loginEditText.getText().toString();
                    activity.authDataObject.authData.pass = loginEditText.getText().toString();
                    progressBar.setVisibility(View.VISIBLE);
                    authorizationPresenter.enterUser(loginEditText.getText().toString(), passwordEditText.getText().toString());
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
        Toast.makeText(activity, getResources().getText(R.string.user_not_registered), Toast.LENGTH_LONG).show();
        progressBar.setVisibility(View.GONE);
    }
}
