package com.dreamteam.httprequest;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;


import com.dreamteam.httprequest.AddOrEditInfoProfile.InfoProfileData;
import com.dreamteam.httprequest.AddOrEditInfoProfile.View.EditInfoProfileController;
import com.dreamteam.httprequest.AutoAndReg.Authorization.Entity.AuthDataObject;
import com.dreamteam.httprequest.AutoAndReg.Authorization.View.AuthorizationController;
import com.dreamteam.httprequest.AutoAndReg.Authorization.View.KeyRegistrationController;
import com.dreamteam.httprequest.AutoAndReg.Authorization.View.RegistrationController;
import com.dreamteam.httprequest.Group.Protocols.ActivityAction;
import com.dreamteam.httprequest.Group.View.GroupController;
import com.dreamteam.httprequest.GroupList.View.GroupsListFragment;
import com.dreamteam.httprequest.Interfaces.OnBackPressedListener;
import com.dreamteam.httprequest.Interfaces.PresenterInterface;
import com.dreamteam.httprequest.ObjectList.ObjectData;
import com.dreamteam.httprequest.ObjectList.View.ObjectListController;
import com.dreamteam.httprequest.SelectedList.SelectData;
import com.dreamteam.httprequest.SelectedList.View.SelectedListController;
import com.dreamteam.httprequest.User.Entity.UserData.User;
import com.dreamteam.httprequest.User.View.UserFragment;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements ActivityAction {

    GroupController groupController;

    public final String userID = "328d21d2-9797-4802-9f5d-0e0b3f204866";

    public BottomNavigationView bottomNavigationView;

    public AuthDataObject authDataObject= new AuthDataObject();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                    switch (menuItem.getItemId()) {

                        case R.id.activities:
                            clearMainActivity();

                        case R.id.groups:
                            clearMainActivity();
//                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new GroupsListFragment()).commit();
                            changeFragment(new GroupsListFragment(), null);
                            break;

                        case R.id.profile:
                            clearMainActivity();
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new UserFragment(userID, true)).commit();
                            break;

                        case R.id.notification:
                            clearMainActivity();
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AuthorizationController()).commit();
                            break;
                    }
                    return true;
                }
            });
    }

    public void clearMainActivity(){

        FragmentManager fm = getSupportFragmentManager();
        int j = fm.getBackStackEntryCount();
        for(int i = 0; i <= (fm.getBackStackEntryCount() + 1); ++i) {
            fm.popBackStack();
        }
        for (Fragment fragment:getSupportFragmentManager().getFragments()) {//TODO: если нажата страница группы, то не удаляет список
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        }
        System.gc();
    }

    public void changeFragment(Fragment fragment, String type) {

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
    }

    public void changeFragmentWitchBackstack(Fragment fragment, String type) {

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment, type).addToBackStack(null).commit();
    }
//    {
//        creatorID : "asda",
//        content : {
//            simpleData : {
//                title : " asd",
//                description : "asdsa"
//            }
//    }
//    }
    public void openGroup(String id){
        GroupController controller = new GroupController(id);
        changeFragmentWitchBackstack(controller, "");//TODO: задать в backstack
    }

    public void openGroupAfterSelect (String id){
        //так как после выбора элементов и совершения действия, нам, при нажатии кнопки назад, надо будет сбросить два шага назад
        FragmentManager fm = getSupportFragmentManager();
        for(int i = 0; i < 20; ++i) {
            fm.popBackStack();
        }
        changeFragmentWitchBackstack(new GroupController(id), null);
    }

    @Override
    public void getGroup(String id) {
        groupController = new GroupController(id);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, groupController, null).addToBackStack(null).commit();
    }


    @Override
    public void showFragment (PresenterInterface delegate, int i){ //TODO: получается метод только для вызова ActivityForResultFragment из-за передачи аргументов

        // Создание класса-фрагмента для отправки интентов, в аргументы передаем Presenter-delegate, выбранную позицию
        ActivityForResultFragment activityForResultFragment = new ActivityForResultFragment(delegate, i);

        //добавляем класс-фрагмент в контейнер, чтобы запустить действия
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, activityForResultFragment)
                .commit();
    }


    public void showDialog(Bundle bundle, PresenterInterface delegate){
        CustomDialogFragment dialogFragment = new CustomDialogFragment(delegate);
        dialogFragment.setArguments(bundle);
        dialogFragment.show(getSupportFragmentManager(), "MyDialog");

    }

    public void showUser (User user){
        changeFragment(new UserFragment(user.id, false), null);
    }

    public void openGroupList(){
        changeFragment(new GroupsListFragment(), null);
    }

    //открыть список с полученными даннами и checkBox
    public void openSelectList (ArrayList<SelectData> selectData,
        PresenterInterface delegate, String TYPE){
        changeFragmentWitchBackstack(new SelectedListController(selectData, delegate, TYPE), null);
    }

    public void openEditProfile (InfoProfileData infoProfileData, PresenterInterface delegate, String type){
        String creatorID = userID;
        changeFragmentWitchBackstack(new EditInfoProfileController(infoProfileData, creatorID, delegate,  type), null);
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();
        OnBackPressedListener backPressedListener = null;
        for (Fragment fragment: fm.getFragments()) {
            if (fragment instanceof  OnBackPressedListener) {
                backPressedListener = (OnBackPressedListener) fragment;
                break;
            }
        }

        if (backPressedListener != null) {
            backPressedListener.onBackPressed();
        } else {
            super.onBackPressed();
        }
    }

    public void hideBottomNavigationView(BottomNavigationView view) {
        view.clearAnimation();
        view.animate().translationY(view.getHeight()).setDuration(300);
    }

    public void showBottomNavigationView(BottomNavigationView view) {
        view.clearAnimation();
        view.animate().translationY(0).setDuration(300);
    }

    public void openProfile(){
        changeFragment(new UserFragment(userID, false), null);
    }

    public void openUser(String userID){
        changeFragmentWitchBackstack(new UserFragment(userID, false), null);
    }


    public void openObjectList(ArrayList<ObjectData> objectDataArrayList, PresenterInterface delegate, String type){
        changeFragmentWitchBackstack(new ObjectListController(objectDataArrayList, type), null);
    }

    public void openRegistration(){
        changeFragmentWitchBackstack(new RegistrationController(), null);
    }

    public void openKeyRegistration(){
        changeFragment(new KeyRegistrationController(), null);
    }

    //метод для получения к заголовку ActionBar'a
    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

}
