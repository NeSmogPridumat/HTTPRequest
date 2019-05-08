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

import com.dreamteam.httprequest.AutoAndReg.Authorization.Entity.AuthData;
import com.dreamteam.httprequest.AutoAndReg.Authorization.Presenter.AuthorizationPresenter;
import com.dreamteam.httprequest.MainActivity;
import com.dreamteam.httprequest.R;

//TODO: порт 9000

//create new login
//enable user auth
//user autoriz

/**
 * A simple {@link Fragment} subclass.
 */
public class AuthorizationController extends Fragment {

    private EditText loginEditText, passwordEditText;
    private Button authorizationButton, registrationButton;

     private AuthorizationPresenter authorizationPresenter;


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
        registrationButton = view.findViewById(R.id.authorization_registration_button);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        MainActivity activity = (MainActivity) getActivity();
        authorizationPresenter = new AuthorizationPresenter(activity);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {

        //кнопка для регистрации, перекидывает на фрагмент для регистрации
        registrationButton.setOnClickListener(new View.OnClickListener() {
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
                    authorizationPresenter.enterUser(loginEditText.getText().toString(), passwordEditText.getText().toString());
                }
            }
        });
        super.onStart();
    }

}
