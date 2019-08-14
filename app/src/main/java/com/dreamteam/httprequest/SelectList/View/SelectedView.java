package com.dreamteam.httprequest.SelectList.View;


import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Toast;

import com.dreamteam.httprequest.GroupList.View.RecyclerItemClickListener;
import com.dreamteam.httprequest.Interfaces.PresenterInterface;
import com.dreamteam.httprequest.MainActivity;
import com.dreamteam.httprequest.R;
import com.dreamteam.httprequest.SelectList.Presenter.SelectPresenter;
import com.dreamteam.httprequest.SelectList.Protocol.SelectView;
import com.dreamteam.httprequest.SelectedList.Data.SelectData;
import com.dreamteam.httprequest.SelectedList.Preseter.SelectListPresenter;
import com.dreamteam.httprequest.SelectedList.Protocols.SelectListViewController;
import com.dreamteam.httprequest.User.Entity.UserData.User;
import com.dreamteam.httprequest.VoitingView.Protocols.SelectViewInterface;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class SelectedView extends Fragment implements SelectView {

    private Button searchButton;
    private EditText searchEdit;
    private RecyclerView recyclerView;
    private View view;
    private SelectPresenter selectPresenter;
    private SelectAdapter selectAdapter;
    private PresenterInterface delegate;
    private ArrayList<SelectData> listObject;
    private String ADD = "Add";


    public SelectedView(PresenterInterface delegate) {
        // Required empty public constructor
        this.delegate = delegate;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_selected_view, container, false);
        searchButton = view.findViewById(R.id.search_button);
        searchEdit = view.findViewById(R.id.search_edit_text);
        recyclerView = view.findViewById(R.id.result_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        this.view = view;
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        selectPresenter = new SelectPresenter(this);
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(searchEdit.getText().toString().equals("")){
                    searchEdit.requestFocus();
                    Toast.makeText(getContext(), "Заполните поле", Toast.LENGTH_LONG).show();
                } else {
                    String name = searchEdit.getText().toString();
                    selectPresenter.getUsers(name);
                }
            }
        });
        super.onStart();

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                selectAdapter.selectCollection.get(position).check = !selectAdapter.selectCollection.get(position).check;
                selectAdapter.notifyItemChanged(position);
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

            //если полученный тип Add (будем что-то куда-то добавлять) то в ActionBar будет кнопка "+"
            inflater.inflate(R.menu.add_select_list_controller, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        ArrayList<SelectData> selectData = new ArrayList<>();
        //циклом формируем список (все у кого checkBox - true)
        for (int i = 0; i < listObject.size(); i++) {
            if (listObject.get(i).check) {
                selectData.add(listObject.get(i));
            }
        }
        switch (item.getItemId()) {
            case R.id.add_user_in_group:
                //отправляем на удаление собранный список
                selectPresenter.inputSelect(delegate, selectData, ADD);
                break;

        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void answerGetUsers(ArrayList<User> users) {
        ArrayList<SelectData> selectDataArrayList = new ArrayList<>();
        for(User user: users) {
            SelectData selectData = new SelectData();
            selectData.title = user.personal.descriptive.name + " " + user.personal.descriptive.surname;
            selectData.id = user.id;
            selectDataArrayList.add(selectData);
        }
        listObject = selectDataArrayList;
        selectAdapter = new SelectAdapter(selectDataArrayList);
        recyclerView.setAdapter(selectAdapter);
        recyclerView.getAdapter();
    }

    @Override
    public void redrawAdapter(String objectID, Bitmap bitmap) {
        if (bitmap != null) {
            selectAdapter.changeItem(objectID, bitmap);
        }
    }
}
