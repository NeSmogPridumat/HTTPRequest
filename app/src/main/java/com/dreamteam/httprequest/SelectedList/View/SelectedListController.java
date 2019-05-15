package com.dreamteam.httprequest.SelectedList.View;


import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.dreamteam.httprequest.Data.ConstantConfig;
import com.dreamteam.httprequest.GroupList.View.RecyclerItemClickListener;
import com.dreamteam.httprequest.Interfaces.PresenterInterface;
import com.dreamteam.httprequest.MainActivity;
import com.dreamteam.httprequest.R;
import com.dreamteam.httprequest.SelectedList.Preseter.SelectListPresenter;
import com.dreamteam.httprequest.SelectedList.Protocols.SelectListViewController;
import com.dreamteam.httprequest.SelectedList.SelectData;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
@SuppressLint("ValidFragment")
public class SelectedListController extends Fragment implements SelectListViewController {

    private ArrayList<SelectData> listObject;
    private String type;
    private final String TAG = "SelectListController";

    private RecyclerView selectRecyclerView;
    private MainActivity activity;

    private SelectAdapter adapter;
    private SelectListPresenter selectListPresenter;
    private PresenterInterface delegate;
    private ConstantConfig constantConfig = new ConstantConfig();

    MenuInflater inflater;
    Menu menu;

    //Передаем конструктуру список и тип действия (удалить, добавить)
    public SelectedListController(ArrayList<SelectData> arrayList, PresenterInterface delegate, String type) {
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
        selectListPresenter = new SelectListPresenter(this, activity);
        activity.setActionBarTitle(type);
    }

    //создаем меню в ActionBar по типу - (корзинка или плюсик)
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        this.inflater = inflater;
        this.menu = menu;
        super.onCreateOptionsMenu(menu, inflater);
        if (type.equals(constantConfig.ADD)) {
            //если полученный тип Add (будем что-то куда-то добавлять) то в ActionBar будет кнопка "+"
            inflater.inflate(R.menu.add_select_list_controller, menu);
        } else if (type.equals(constantConfig.DELETE)){
            //если тип Delete, будет корзина
            inflater.inflate(R.menu.delete_select_list_controller, menu);
        } else if (type.equals(constantConfig.ADMIN)){
            inflater.inflate(R.menu.one_change_select_list, menu);
        }
    }

    //при нажатии на корзину, собираем список выбранных элементов и отправляем в презентер
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
            //если нажата кнопка remove, список отправляется на удаление
            case R.id.remove_select_list_edit:
                //отправляем на удаление собранный список
                selectListPresenter.inputSelect(delegate, selectData, type);
                break;
            case R.id.add_user_in_group:

                //отправляем на удаление собранный список
                selectListPresenter.inputSelect(delegate, selectData, type);
                break;

            case R.id.one_select_list_edit:

                //отправляем на удаление собранный список
                selectListPresenter.inputSelect(delegate, selectData, type);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        adapter.selectCollection = listObject;
        selectRecyclerView.setAdapter(adapter);

        //вешаем слушатель на список (меняем занчение check)
        if (type.equals(constantConfig.ADMIN)){
            selectRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), selectRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    for (int i = 0; i < listObject.size(); i++) {
                        listObject.get(i).check = false;
                    }
                    listObject.get(position).check = !listObject.get(position).check;
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onLongItemClick(View view, int position) {

                }
            }));
        } else {
            selectRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), selectRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    listObject.get(position).check = !listObject.get(position).check;
                    adapter.notifyItemChanged(position);
                }

                @Override
                public void onLongItemClick(View view, int position) {

                }
            }));
        }
        getImage(listObject);
        super.onStart();
    }

    private void getImage(ArrayList<SelectData> arrayList){
        for(int i = 0; i < arrayList.size(); i++){
            if (arrayList.get(i).image == null && !(arrayList.get(i).imageURL.equals(null))){
                selectListPresenter.getImage(arrayList.get(i).id, arrayList.get(i).imageURL);
            }
        }
    }

    @Override
    public void redrawAdapter(String groupID, Bitmap bitmap) {
        if (bitmap != null) {
            adapter.changeItem(groupID, bitmap);
        }
    }

    @Override
    public void error(String title, String description) {
        Toast.makeText(activity, title + "\n" + description, Toast.LENGTH_LONG).show();
    }

    //TODO: внедрить измененное состояние для флажка и синхронизировать недавно обновленное состояние с флагом isChecked текущего объекта. Когда вы связываете свой держатель вида, проверьте, является ли флаг истинным или ложным, и обновите макет в соответствии с флагом.
}
