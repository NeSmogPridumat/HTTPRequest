package com.dreamteam.httprequest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.dreamteam.httprequest.AddOrEditInfoProfile.Data.InfoProfileData;
import com.dreamteam.httprequest.AddOrEditInfoProfile.View.EditInfoProfileController;
import com.dreamteam.httprequest.AutoAndReg.Authorization.Entity.AuthDataObject;
import com.dreamteam.httprequest.AutoAndReg.Authorization.View.AuthorizationController;
import com.dreamteam.httprequest.AutoAndReg.Authorization.View.KeyRegistrationController;
import com.dreamteam.httprequest.AutoAndReg.Authorization.View.RegistrationController;
import com.dreamteam.httprequest.Data.RequestInfo;
import com.dreamteam.httprequest.Dialog.CustomDialogFragment;
import com.dreamteam.httprequest.Event.Entity.EventType4.EventType4;
import com.dreamteam.httprequest.Event.View.EventType4Controller;
import com.dreamteam.httprequest.EventList.View.EventListController;
import com.dreamteam.httprequest.Group.Protocols.ActivityAction;
import com.dreamteam.httprequest.Group.View.GroupController;
import com.dreamteam.httprequest.GroupList.View.GroupsListFragment;
import com.dreamteam.httprequest.Interfaces.OnBackPressedListener;
import com.dreamteam.httprequest.Interfaces.PresenterInterface;
import com.dreamteam.httprequest.ObjectList.ObjectData;
import com.dreamteam.httprequest.ObjectList.View.ObjectListController;
import com.dreamteam.httprequest.SelectedList.SelectData;
import com.dreamteam.httprequest.SelectedList.View.SelectedListController;
import com.dreamteam.httprequest.Service.EventService;
import com.dreamteam.httprequest.User.Entity.UserData.User;
import com.dreamteam.httprequest.User.View.UserFragment;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements ActivityAction {

    public BottomNavigationView bottomNavigationView;
    public TextView bottomNavigationTextView;

    public String userID;

    public AuthDataObject authDataObject;

    public final String BROADCAST_ACTION = "com.dreamteam.httprequest";

    BroadcastReceiver br;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        authDataObject = new AuthDataObject();

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()) {

                    case R.id.groups:
                        clearMainActivity();
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, GroupsListFragment.newInstance(userID))
                                .commit();
                        break;

                    case R.id.profile:
                        clearMainActivity();
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, UserFragment.newInstance(userID))
                                .commit();
                        break;

                    case R.id.notification:
                        clearMainActivity();
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, EventListController.newInstance(userID))
                                .commit();
                        break;

                    case R.id.contacts:
                        clearMainActivity();
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, new BlankTest() )
                                .commit();
                        break;

                    case R.id.profile2:
                        clearMainActivity();
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, ChuvakEtoRepchik.newInstance(userID))
                                .commit();
                        break;
                }
                return true;
                }
            });
        userID = QueryPreferences.getUserIdPreferences(getBaseContext());
        if (!(userID.equals(""))) {
            bottomNavigationView.setSelectedItemId(R.id.profile);

        } else {
            bottomNavigationView.setVisibility(View.GONE);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new AuthorizationController(this)).commit();
        }

        EventService.setServiceAlarm(getBaseContext()); }

    @Override
    protected void onStart() {

//        // Создаем PendingIntent для Task1
//        Intent intent = new Intent(this, EventService.class);
//        PendingIntent pi = createPendingResult(1, intent, 0);
//        // Создаем Intent для вызова сервиса, кладем туда параметр времени
//        // и созданный PendingIntent
//        intent.putExtra("userID", userID)
//                .putExtra("Pending", pi);
//        // стартуем сервис
////        startService(intent);
//
        BottomNavigationMenuView bottomNavigationMenuView =
                (BottomNavigationMenuView) bottomNavigationView.getChildAt(0);
        View v = bottomNavigationMenuView.getChildAt(1);
        BottomNavigationItemView itemView = (BottomNavigationItemView) v;
//
        View badge = LayoutInflater.from(this)
                .inflate(R.layout.bottomnavigation_event_notification, itemView, true);
        bottomNavigationTextView = badge.findViewById(R.id.notification_badge);
//
//        bottomNavigationTextView.setVisibility(View.INVISIBLE);

        // создаем BroadcastReceiver, для прослушки отправляемых сервисом Intent'ов
        br = new BroadcastReceiver() {
            // действия при получении сообщений
            public void onReceive(Context context, Intent intent) {
                int activeEvent = intent.getIntExtra("event", 0);
                if(activeEvent != 0){
                    bottomNavigationTextView.setText(String.valueOf(activeEvent));
                }
            }
        };

        // создаем фильтр для BroadcastReceiver
        IntentFilter intFilt = new IntentFilter(BROADCAST_ACTION);
        // регистрируем (включаем) BroadcastReceiver
        registerReceiver(br, intFilt);

        super.onStart();
    }

    public void clearMainActivity() {
        FragmentManager fm = getSupportFragmentManager();
        for (int i = 0; i <= (fm.getBackStackEntryCount() + 1); ++i) {
            fm.popBackStack();
        }
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {//TODO: если нажата страница группы, то не удаляет список
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        }
        System.gc();
    }

    public void changeFragment(Fragment fragment, String type) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
    }

    public void changeFragmentWitchBackstack(Fragment fragment, String type) {

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment, type)
                .addToBackStack(null).commit();
    }

    public void openGroup(String id, int rules) {
        changeFragmentWitchBackstack(GroupController.newInstance(id, rules), "");
    }

    public void openGroupAfterSelect(String id, int rules) {
        //так как после выбора элементов и совершения действия, нам, при нажатии кнопки назад, надо будет сбросить два шага назад
        FragmentManager fm = getSupportFragmentManager();
        for (int i = 0; i < 20; ++i) {
            fm.popBackStack();
        }
        changeFragmentWitchBackstack(GroupController.newInstance(id, rules), null);
    }

    public void getGroup(String id, int rules) {
        changeFragmentWitchBackstack(GroupController.newInstance(id, rules), null);
    }

    @Override
    public void showFragment(PresenterInterface delegate, int i) { //TODO: получается метод только для вызова ActivityForResultFragment из-за передачи аргументов

        // Создание класса-фрагмента для отправки интентов, в аргументы передаем Presenter-delegate, выбранную позицию
        ActivityForResultFragment activityForResultFragment = new ActivityForResultFragment(delegate, i);

        //добавляем класс-фрагмент в контейнер, чтобы запустить действия
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, activityForResultFragment)
                .commit();
    }

    public void showDialog(Bundle bundle, PresenterInterface delegate) {
        CustomDialogFragment dialogFragment = new CustomDialogFragment(delegate, this);
        dialogFragment.setArguments(bundle);
        dialogFragment.show(getSupportFragmentManager(), "MyDialog");

    }

    public void showUser(User user) {
        changeFragment(UserFragment.newInstance(userID), null);
    }

    public void openGroupList() {
        changeFragment(GroupsListFragment.newInstance(userID), null);
    }

    //открыть список с полученными даннами и checkBox
    public void openSelectList(ArrayList<SelectData> selectData,
                               PresenterInterface delegate, String TYPE) {
        changeFragmentWitchBackstack(new SelectedListController(selectData, delegate, TYPE), null);
    }

    public void openEditProfile(InfoProfileData infoProfileData, RequestInfo requestInfo,
                                PresenterInterface delegate, String type) {
        changeFragmentWitchBackstack(new EditInfoProfileController(infoProfileData, requestInfo,
                delegate, type), null);
    }

    //переопределили метод, чтобы можно было обрабатывать нажатие кнопки Back во фрагментах
    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();
        OnBackPressedListener backPressedListener = null;
        for (Fragment fragment : fm.getFragments()) {
            if (fragment instanceof OnBackPressedListener) {
                backPressedListener = (OnBackPressedListener) fragment;
                break;
            }
            int z = 0;
            int x = (z==0)?100:200;//TODO: интересная функция, которую можно использовать (можно ли использовать для вызова методов?)
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
        bottomNavigationView.setVisibility(View.GONE);
    }

    public void showBottomNavigationView(BottomNavigationView view) {
        bottomNavigationView.setVisibility(View.VISIBLE);
        view.clearAnimation();
        view.animate().translationY(0).setDuration(300);
    }

    public void openProfile() {
        changeFragment(UserFragment.newInstance(userID), null);
    }

    public void openUser(String userID) {
        changeFragmentWitchBackstack(UserFragment.newInstance(userID), null);
    }

    public void openObjectList(ArrayList<ObjectData> objectDataArrayList, PresenterInterface delegate,
                               String type) {
        changeFragmentWitchBackstack(new ObjectListController(objectDataArrayList, type), null);
    }

    //открытие контроллера для регистрации
    public void openRegistration() {
        changeFragmentWitchBackstack(new RegistrationController(), null);
    }

    //открытие контроллера для ввода ключа регистрации
    public void openKeyRegistration() {
        changeFragment(new KeyRegistrationController(), null);
    }

    //метод для получения к заголовку ActionBar'a
    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    public void saveSharedPreferences(String userID) {//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        //с помощью метода getPreferences получаем объект sPref класса SharedPreferences, который позволяет работать с данными (читать и писать).Константа MODE_PRIVATE используется для настройки доступа и означает, что после сохранения, данные будут видны только этому приложению
//        sharedPreferences = getPreferences(MODE_PRIVATE);
//
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//
//        editor.putString("userID", userID);
//        editor.apply();

        QueryPreferences.saveSharedPreferences(userID, this);
    }

    public void exitLogin() {
        QueryPreferences.exitLoginPreferences(this);
//        sharedPreferences = getPreferences(MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.clear();
//        editor.apply();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new AuthorizationController(this)).commit();
    }

    public void openEventType4(EventType4 event) {
        changeFragmentWitchBackstack(new EventType4Controller(event), null);
    }

    public void openEventList() {
        changeFragment(EventListController.newInstance(userID), null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == 1) {
//            String event = data.getStringExtra("Events");
//            if (event.equals("0")) {
//                bottomNavigationTextView.setVisibility(View.GONE);
//            } else {
//                bottomNavigationTextView.setText(event);
//                bottomNavigationTextView.setVisibility(View.VISIBLE);
//            }
//        }
    }

    public void showImage (Drawable drawable){
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, new BlankFragment(drawable, this), null)
                .addToBackStack(null).commit();
//        changeFragmentWitchBackstack(new BlankFragment(drawable, this), null);
    }

    public void deleteBackStack() {
            FragmentManager fm = getSupportFragmentManager();
        for(int i = 0; i < 2; ++i) {
            fm.popBackStack();
        }
    }

    @Override
    protected void onStop() {

        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // дерегистрируем (выключаем) BroadcastReceiver
        unregisterReceiver(br);

//        unbindService(serviceConnection);
    }
}
