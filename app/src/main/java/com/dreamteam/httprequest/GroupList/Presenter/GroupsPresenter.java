package com.dreamteam.httprequest.GroupList.Presenter;

import android.graphics.Bitmap;
import android.util.Log;

import com.dreamteam.httprequest.AddOrEditInfoProfile.Data.InfoProfileData;
import com.dreamteam.httprequest.Data.ConstantConfig;
import com.dreamteam.httprequest.Data.RequestInfo;
import com.dreamteam.httprequest.Group.Entity.GroupData.Group;
import com.dreamteam.httprequest.Group.Entity.GroupData.Personal;
import com.dreamteam.httprequest.GroupList.Interactor.GroupsInteractor;
import com.dreamteam.httprequest.GroupList.Protocols.GroupsPresenterInterface;
import com.dreamteam.httprequest.GroupList.Protocols.GroupsViewInterface;
import com.dreamteam.httprequest.GroupList.RouterGroupList;
import com.dreamteam.httprequest.MainActivity;
import com.dreamteam.httprequest.SelectedList.Data.SelectData;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
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
    public void error(Throwable t) {
        delegate.error(t);
    }

    @Override
    public void answerAddGroup(Group group) {
        groupsInteractor.postSubscription(group.id);

        //TODO: ВОзможно сделать сразу попадание в группу (проблема с BackStack
        routerGroupList.showGroupList();


    }

    public void getGroups(String id) {
        groupsInteractor.getGroups(id);
    }

    public void showAddGroup(){
        routerGroupList.showAddGroup(null, this, constantConfig.ADD_GROUP_TYPE);
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
    public void answerDialog(int i, String title, String message, String priority) {
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
        group.personal = new Personal();
        group.personal.descriptive.title = infoProfileData.title;
        group.personal.descriptive.description = infoProfileData.description;
        if (requestInfo == null){
            requestInfo = new RequestInfo();
        }
        requestInfo.creatorID = activity.userID;

        File imageFile = null;
        if(infoProfileData.imageData != null) {
            Bitmap bitmap = infoProfileData.imageData;
            imageFile = getFileinBitmap(bitmap);
        }
        groupsInteractor.addGroup(group, imageFile, requestInfo);
    }

    public void openGroup(String id){
        routerGroupList.openGroup(id);
    }

    private File getFileinBitmap (Bitmap bitmap){
        OutputStream os;
        File filesDir = activity.getFilesDir();
        File imageFile = new File(filesDir, "file" + ".jpg");
        try {
            os = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
            os.flush();
            os.close();
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Error writing bitmap", e);
        }
        return imageFile;
    }

    public void getImage(ArrayList<Group> groups){
        for (int i = 0; i< groups.size(); i++) {
            groupsInteractor.getImageRequest(groups.get(i).id);
        }
    }


}




