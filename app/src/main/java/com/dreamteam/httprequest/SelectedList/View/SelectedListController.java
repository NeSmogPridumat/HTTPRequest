package com.dreamteam.httprequest.SelectedList.View;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dreamteam.httprequest.Data.ConstantConfig;
import com.dreamteam.httprequest.GroupList.View.RecyclerItemClickListener;
import com.dreamteam.httprequest.Interfaces.PresenterInterface;
import com.dreamteam.httprequest.MainActivity;
import com.dreamteam.httprequest.R;
import com.dreamteam.httprequest.SelectedList.Preseter.SelectListPresenter;
import com.dreamteam.httprequest.SelectedList.Protocols.SelectListViewController;
import com.dreamteam.httprequest.SelectedList.Data.SelectData;

import java.net.SocketTimeoutException;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
@SuppressLint("ValidFragment")
public class SelectedListController extends Fragment implements SelectListViewController {

    private ArrayList<SelectData> listObject;
    private String type;

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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
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
        MenuItem search1 = menu.findItem(R.id.app_bar_search);
        final SearchView searchView = (SearchView) search1.getActionView();
        search(searchView);
    }

    private void search(SearchView searchView) {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
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
                    for (int i = 0; i < adapter.mFilteredList.size(); i++) {
                        adapter.mFilteredList.get(i).check = false;
                    }
                    adapter.mFilteredList.get(position).check = !adapter.mFilteredList.get(position).check;
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
                    adapter.mFilteredList.get(position).check = !adapter.mFilteredList.get(position).check;
                    adapter.notifyItemChanged(position);
                }

                @Override
                public void onLongItemClick(View view, int position) {

                }
            }));
        }
        //getImage(listObject);
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
    public void error(Throwable t) {
        String title = null;
        String description = null;
        if (t instanceof SocketTimeoutException) {
            title = getResources().getString(R.string.error_connecting_to_server);
            description = getResources().getString(R.string.check_the_connection_to_the_internet);
        }else if (t instanceof NullPointerException) {
            title = getResources().getString(R.string.object_not_found);
            description = "";
        }
        Toast.makeText(activity, title + "\n" + description, Toast.LENGTH_LONG).show();
    }

    //TODO: внедрить измененное состояние для флажка и синхронизировать недавно обновленное состояние с флагом isChecked текущего объекта. Когда вы связываете свой держатель вида, проверьте, является ли флаг истинным или ложным, и обновите макет в соответствии с флагом.
}
