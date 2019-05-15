package com.dreamteam.httprequest.GroupList.Presenter;

import android.graphics.Bitmap;
import android.widget.Toast;

import com.dreamteam.httprequest.AddOrEditInfoProfile.InfoProfileData;
import com.dreamteam.httprequest.Data.ConstantConfig;
import com.dreamteam.httprequest.Data.RequestInfo;
import com.dreamteam.httprequest.Group.Entity.GroupData.Group;
import com.dreamteam.httprequest.GroupList.Interactor.GroupsInteractor;
import com.dreamteam.httprequest.GroupList.Protocols.GroupsPresenterInterface;
import com.dreamteam.httprequest.GroupList.Protocols.GroupsViewInterface;
import com.dreamteam.httprequest.GroupList.RouterGroupList;
import com.dreamteam.httprequest.MainActivity;
import com.dreamteam.httprequest.SelectedList.SelectData;

import java.util.ArrayList;

public class GroupsPresenter implements GroupsPresenterInterface {

    private GroupsViewInterface delegate;
    private MainActivity activity;
    private GroupsInteractor groupsInteractor = new GroupsInteractor(this);
    private RouterGroupList routerGroupList;
    private ConstantConfig constantConfig = new ConstantConfig();

    public GroupsPresenter(GroupsViewInterface delegate, MainActivity activity){
        this.delegate = delegate;
        this.activity = activity;
        routerGroupList = new RouterGroupList(activity);
    }

    @Override
    public void answerGetImageGroups(String groupID, Bitmap bitmap) {
        delegate.redrawAdapter(groupID, bitmap);
    }

    @Override
    public void answerGetGroups(ArrayList<Group> groupCollection) {
        delegate.outputGroupsView(groupCollection);
    }

    @Override
    public void answerDeleteGroups() {
        routerGroupList.showGroupList();
    }

    @Override
    public void error(String title, String description) {
        delegate.error(title, description);
    }

    @Override
    public void answerAddGroup() {
        routerGroupList.showGroupList();
    }

    public void getGroups(String id) {
        groupsInteractor.getGroups(id);
    }

    public void showAddGroup(){
        InfoProfileData infoProfileData = null;
        routerGroupList.showAddGroup(infoProfileData, this, constantConfig.GROUP_TYPE);
    }


    //отправляем запрос на показ списка с checkBox
    public void showSelectedList(ArrayList<Group> groups, MainActivity activity, String TYPE){
        ArrayList<SelectData> selectData = new ArrayList<>();
        for (int i = 0; i < groups.size(); i++){
            selectData.add(new SelectData().initFromGroup(groups.get(i)));
        }
        routerGroupList.showSelectList(selectData, this, activity, TYPE);
    }

    @Override
    public void showDialog() {
        //для показа диалога
    }

    @Override
    public void answerDialog(int i) {
        //получение ответа от диалога
    }

    @Override
    public void forResult(Bitmap bitmap) {

    }

    //ответ на список select
    @Override
    public void inputSelect(ArrayList<SelectData> arrayList, String type) {
        groupsInteractor.inputSelect(arrayList, type);

    }

    @Override
    public void editInfo(InfoProfileData infoProfileData, RequestInfo requestInfo, String type) {
        Group group = new Group();
        group.content.simpleData.title = infoProfileData.title;
        group.content.simpleData.description = infoProfileData.description;
        if (requestInfo == null){
            requestInfo = new RequestInfo();
        }
        requestInfo.creatorID = activity.userID;

        Bitmap bitmap = infoProfileData.imageData;
        groupsInteractor.addGroup(group, bitmap, requestInfo);
    }

    public void openGroup(String id, int rules){
        routerGroupList.openGroup(id, rules);
    }
}




