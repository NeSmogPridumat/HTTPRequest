package com.dreamteam.httprequest.SelectedList.View;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.dreamteam.httprequest.GroupList.View.RecyclerItemClickListener;
import com.dreamteam.httprequest.Interfaces.PresenterInterface;
import com.dreamteam.httprequest.MainActivity;
import com.dreamteam.httprequest.R;
import com.dreamteam.httprequest.SelectedList.Preseter.SelectListPresenter;
import com.dreamteam.httprequest.SelectedList.SelectListData;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
@SuppressLint("ValidFragment")
public class SelectedListController extends Fragment {

    ArrayList<SelectListData> listObject = new ArrayList<>();
    String type;
    final String ADD = "Add";
    final String DELETE = "Delete";
    private final String TAG = "SelectListController";

    private RecyclerView selectRecyclerView;
    MainActivity activity;

    private SelectAdapter adapter;
    SelectListPresenter selectListPresenter;
    private PresenterInterface delegate;

    MenuInflater inflater;
    Menu menu;

    SparseBooleanArray checkedArray = new SparseBooleanArray();

    //Передаем конструктуру список и тип действия (удалить, добавить)
    public SelectedListController(ArrayList<SelectListData> arrayList, PresenterInterface delegate, String type) {
        // Required empty public constructor
        this.listObject = arrayList;
        this.type = type;
        this.delegate = delegate;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_selected_list_controller, container, false);

        selectRecyclerView = view.findViewById(R.id.selected_recycler_view);
        selectRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        activity = (MainActivity) getActivity();

        adapter = new SelectAdapter(listObject);
        selectListPresenter = new SelectListPresenter(activity);
    }

    //создаем меню в ActionBar по типу - (корзинка или плюсик)
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        this.inflater = inflater;
        this.menu = menu;
        super.onCreateOptionsMenu(menu, inflater);
        if (type.equals(ADD)) {
            //если полученный тип Add (будем что-то куда-то добавлять) то в ActionBar будет кнопка "+"
            inflater.inflate(R.menu.group_list_controller, menu);
        } else if (type.equals(DELETE)){
            //если тип Delete, будет корзина
            inflater.inflate(R.menu.delete_group_list_controller, menu);
        }
    }

    //при нажатии на корзину, собираем список выбранных элементов и отправляем в презентер
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            //если нажата кнопка remove, список отправляется на удаление
            case R.id.remove_select_list_edit:
                ArrayList<SelectListData> deleteSelect = new ArrayList<>();

                //циклом формируем список на удаление (все у кого checkBox - true)
                for (int i = 0; i < listObject.size(); i++) {
                    if (listObject.get(i).check) {
                        deleteSelect.add(listObject.get(i));
                    }
                }

                //отправляем на удаление собранный список
                selectListPresenter.inputSelect(delegate, deleteSelect, type);

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        adapter.selectCollection = listObject;
        selectRecyclerView.setAdapter(adapter);

        //вешаем слушатель на список (меняем занчение check)
        selectRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), selectRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Log.i("Shhhhiiiit", String.valueOf(position));
                if (listObject.get(position).check){
                    listObject.get(position).check = false;
                } else {
                    listObject.get(position).check = true;
                }
                adapter.notifyItemChanged(position);
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));
        super.onStart();
    }

    //TODO: внедрить измененное состояние для флажка и синхронизировать недавно обновленное состояние с флагом isChecked текущего объекта. Когда вы связываете свой держатель вида, проверьте, является ли флаг истинным или ложным, и обновите макет в соответствии с флагом.
}
