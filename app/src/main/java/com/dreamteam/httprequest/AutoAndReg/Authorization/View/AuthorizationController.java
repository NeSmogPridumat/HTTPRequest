package com.dreamteam.httprequest.AutoAndReg.Authorization.View;


import android.annotation.SuppressLint;
import android.content.Intent;
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
                if ((loginEditText.getText().toString().equals("")))//-----------------------------------проверка на заполнение поля
                {
                    loginEditText.setError("Заполните поле");
                    loginEditText.requestFocus();
                } else if (passwordEditText.getText().toString().equals("")){
                    passwordEditText.setError("Заполниет поле");
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
    public void error(String title, String description) {
        Toast.makeText(activity, title + "\n" + description, Toast.LENGTH_LONG).show();
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void showNotFound() {
        Toast.makeText(activity, "Пользователь не зарегестрирован", Toast.LENGTH_LONG).show();
        progressBar.setVisibility(View.GONE);
    }
}
