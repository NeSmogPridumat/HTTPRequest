package com.dreamteam.httprequest;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.dreamteam.httprequest.Data.ConstantConfig;
import com.dreamteam.httprequest.Data.HTTPConfig;
import com.dreamteam.httprequest.Event.Entity.EventType4.EventType4;
import com.dreamteam.httprequest.EventList.Interactor.EventListInteractor;
import com.dreamteam.httprequest.EventList.Protocols.EventListFromHTTPManagerInterface;
import com.dreamteam.httprequest.HTTPManager.HTTPManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.util.ArrayList;

public class BlankTest extends Fragment implements EventListFromHTTPManagerInterface {
    // TODO: Rename parameter arguments, choose names that match

    private ListView listView;

    private static final int REQUEST_CODE_READ_CONTACTS=1;
    private static boolean READ_CONTACTS_GRANTED =false;

    ArrayList<String> contacts = new ArrayList<String>();
    HTTPManager httpManager = HTTPManager.get();
    HTTPConfig httpConfig = new HTTPConfig();
    ConstantConfig constantConfig = new ConstantConfig();


    public BlankTest() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_blank_test, container, false);
        listView = view.findViewById(R.id.contacts);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onStart() {
        // получаем разрешения
        int hasReadContactPermission = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_CONTACTS);
        // если устройство до API 23, устанавливаем разрешение
        if(hasReadContactPermission == PackageManager.PERMISSION_GRANTED){
            READ_CONTACTS_GRANTED = true;
        }
        else{
            // вызываем диалоговое окно для установки разрешений
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_CODE_READ_CONTACTS);
        }
        // если разрешение установлено, загружаем контакты
        if (READ_CONTACTS_GRANTED){
            loadContacts();
        }

        getEvents("004b55ac-6e43-4662-8cc0-9169b6537ae6");
        super.onStart();
    }

    private void loadContacts(){
        Cursor cursor = getActivity().getContentResolver()
                .query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[] {ContactsContract.CommonDataKinds.Phone._ID,
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                        ContactsContract.CommonDataKinds.Phone.NUMBER},
                        null,
                        null,
                        null);
        getActivity().startManagingCursor(cursor);
        while (cursor.moveToNext())
        {
            contacts.add(" ID "+cursor.getString(0)+" NAME "
                    + cursor.getString(1) + " \nPHONE "+cursor.getString(2));
        }

        listView.setAdapter(new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_list_item_activated_1,
                contacts));
    }

    public void getEvents(String groupId){
        final String path = httpConfig.serverURL + httpConfig.SERVER_GETTER + httpConfig.EVENT
                + httpConfig.GROUP + httpConfig.GROUP_ID_PARAM + groupId;
        //TODO путь для получения списка эвентов

        new Thread(new Runnable() {
            @Override
            public void run() {
                httpManager.getRequest(path, constantConfig.GET_EVENT_TYPE,
                        BlankTest.this);
            }
        }).start();
    }

    @Override
    public void response(byte[] byteArray, String type) {
        Log.i("Кавбага", "Ееееее!!");
        if (byteArray != null){
            final ArrayList<EventType4> eventArrayList;
            try {
                eventArrayList = createEventsOfBytes(byteArray);
                if (eventArrayList == null){

                } else {
                    Handler mainHandler = new Handler(Looper.getMainLooper());
                    Runnable myRunnable = new Runnable() {
                        @Override
                        public void run() {
                            Log.i("ЫЫЫЫЫЫ", eventArrayList.get(0).id + "     " + eventArrayList.get(1).id + "     " + eventArrayList.get(2).id + "     ");
                        }
                    };
                    mainHandler.post(myRunnable);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void error(Throwable t) {
        Log.i("Tew", "Туц");
    }

    @Override
    public void errorHanding(int resposeCode, String type) {
        Log.i("Tew", "Туц2");
    }

    private ArrayList<EventType4> createEventsOfBytes (byte[] byteArray)throws Exception {

        Gson gson = new Gson();
        String jsonString = new String(byteArray);
        JSONArray jsonArray = new JSONArray(jsonString);
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            list.add(jsonArray.getString(i));
        }

        ArrayList<EventType4> events = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            if (!(jsonArray.get(i).equals("null"))) {
                EventType4 event = gson.fromJson((list.get(i)), new TypeToken<EventType4>() {
                }.getType());
                events.add(event);
            }
        }
        return events;
    }
}



