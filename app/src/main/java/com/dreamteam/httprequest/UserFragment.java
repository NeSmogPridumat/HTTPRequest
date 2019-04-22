package com.dreamteam.postget;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dreamteam.postget.Data.GetUserClass;
import com.dreamteam.postget.Data.GetUserInterface;
import com.dreamteam.postget.Data.GetUsersClass;
import com.dreamteam.postget.Data.GetUsersInterface;
import com.dreamteam.postget.Data.ResponceInterface;
import com.dreamteam.postget.Support.Config;
import com.dreamteam.postget.User.User;
import com.dreamteam.postget.UserDel.UserDel;
import com.squareup.picasso.Picasso;

import java.net.SocketTimeoutException;

import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class UserFragment extends Fragment  {

    ImageView userImage, raitingStoryImage, scheduleImage;
    TextView userName, userSurName, mail, call, rating, groupTitle;

    public UserInteractor userInteractor;
    private Config config;
    private GetUserClass getUserClass = new GetUserClass();
    private GetUsersClass getUsersClass = new GetUsersClass();
    String id;

    //User thisUser = new User();


    public UserFragment() {
        // Required empty public constructor
        this.config = new Config();
        userInteractor = new UserInteractor();
        id = config.getUserId();
        String url = this.config.serverUrl;


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user, container, false);
        userName = view.findViewById(R.id.user_name_text_view);
        userSurName = view.findViewById(R.id.user_surname_text_view);
        mail = view.findViewById(R.id.mail_text_view);
        call = view.findViewById(R.id.call_number_text_view);
        rating = view.findViewById(R.id.rating_text_view);
        groupTitle = view.findViewById(R.id.group_title);
        userImage = view.findViewById(R.id.user_image);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        //id = "user?id=" + id;

        //httpController.Test(id);

        userInteractor.startGetUser(id);
//
        String users = "users";
 //       httpController.getResponseTest(users, "getUsers");

//        ClassA classA = new ClassA();
//        ClassB classB = new ClassB();
//
//        ClassC classC = new ClassC();
//        classC.callPrint(classB);

    }

    public static UserFragment newInstance() {
        return new UserFragment();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.user_fragment, menu);
    }


    //@Override
    //public void getUser(User user) {
    //    if (user == null){
    //        error(new NullPointerException());
    //    } else {
    //        userName.setText(user.content.simpleData.name);
    //        userSurName.setText(user.content.simpleData.surname);
    //        Picasso.get()
    //                .load(this.config.serverImageUrl + user.content.mediaData.image)
    //                .error(R.drawable.ic_account_box_black_error)
    //                .into(userImage);
    //    }
    //}

//    public void responceGetUser(Response response){
//        User user = new User();
//        user = (User) response.body();
//        if (user == null) {
//            error(new NullPointerException());
//        } else {
//            userName.setText(user.content.simpleData.name);
//            userSurName.setText(user.content.simpleData.surname);
//            Picasso.get()
//                    .load(this.config.serverImageUrl + user.content.mediaData.image)
//                    .error(R.drawable.ic_account_box_black_error)
//                    .into(userImage);
        }


//    public void responceGetUsers(Response response){



//    @Override
//    public void getResponse(Response response, String type) {
//        if (type == "getUser") {
//            this.responceGetUser(response);
//        } else if ( type == "getUsers"){
//            this.responceGetUsers(response);
//        }
//    }

//    @Override
//    public void createUser(User user) {
//
//    }
//
//    @Override
//    public void updateUser(User user) {
//
//    }
//
//    @Override
//    public void deleteUser(UserDel userDel) {
//
//    }
//
//    @Override
//    public void error(Throwable t) {
//        String s = null;
//        if(t == null){
//            s = "Throwbale is null";
//        }else if (t instanceof SocketTimeoutException){
//            s = "Ошибка ожидания сервера";
//        }else if (t instanceof NullPointerException){
//            s = "Нет объекта";
//        }
//
//
//        Log.i("UserFragment", s);
//    }
//    }





