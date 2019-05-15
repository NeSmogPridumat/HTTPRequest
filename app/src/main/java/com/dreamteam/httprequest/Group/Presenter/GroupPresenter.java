package com.dreamteam.httprequest.Group.Presenter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Toast;

import com.dreamteam.httprequest.AddOrEditInfoProfile.InfoProfileData;
import com.dreamteam.httprequest.Data.AddData;
import com.dreamteam.httprequest.Data.ConstantConfig;
import com.dreamteam.httprequest.Data.RequestInfo;
import com.dreamteam.httprequest.Event.Entity.EventType4.EventType4;
import com.dreamteam.httprequest.Group.Entity.GroupData.Group;
import com.dreamteam.httprequest.Group.GroupRouter;
import com.dreamteam.httprequest.Group.Interactor.GroupInteractor;
import com.dreamteam.httprequest.Group.Protocols.GroupPresenterInterface;
import com.dreamteam.httprequest.Group.Protocols.GroupViewInterface;
import com.dreamteam.httprequest.GroupList.Protocols.Router;
import com.dreamteam.httprequest.MainActivity;
import com.dreamteam.httprequest.ObjectList.ObjectData;
import com.dreamteam.httprequest.SelectedList.SelectData;
import com.dreamteam.httprequest.User.Entity.UserData.User;

import java.util.ArrayList;

public class GroupPresenter implements GroupPresenterInterface {
    private GroupViewInterface delegate;
    private GroupRouter router;
    private GroupInteractor groupInteractor = new GroupInteractor(this);
    private String groupID;
    private String userID;
    private ConstantConfig constantConfig = new ConstantConfig();
    private MainActivity activity;

    public GroupPresenter(GroupViewInterface delegate, MainActivity activity){
        this.delegate = delegate;
        this.activity = activity;
        router = new GroupRouter(activity);
    }

    //============================ОТПРАВКА В INTERACTOR==========================================//

    public void getGroup(String id){
        userID = activity.userID;
        groupInteractor.getGroup(id, userID);
        Log.i("GROUP_PRESENTER", id);
        groupID = id;
    }

    @Override
    public void error(String error) {
        delegate.error(error);
    }

    @Override
    public void answerGetGroup(Group group) {
        delegate.outputGroupView(group);
    }

    @Override
    public void answerGetImage(Bitmap bitmap) {
        delegate.outputImageView(bitmap);
    }

    @Override
    public void answerGetMembers(ArrayList<User> members, String type) {
        delegate.outputMembersView(members);
    }

    @Override
    public void answerGetMembersForList(ArrayList<ObjectData> arrayList) {
        router.showMembersList(arrayList, this, constantConfig.USER_TYPE);
    }



    @Override
    public void answerGetUsersForSelect(ArrayList<User> users, String type) {
        ArrayList<SelectData> selectData = new ArrayList<>();
        for (int i = 0; i < users.size(); i++){
            selectData.add(new SelectData().initFromUser(users.get(i)));
        }
        router.showSelectList(selectData, this, type);
    }

    @Override
    public void openGroupsList() {
        router.openGroupsList();
//        Toast.makeText(activity, "Группа будет удалена после согласия создателя группы", Toast.LENGTH_LONG).show();
    }

    @Override
    public void answerAddGroup(EventType4 event) {
        groupInteractor.getGroupAfterEdit(event.data.groupCreatorID, userID);
    }

    @Override
    public void openGroupAfterSelect() {
        router.openGroupAfterSelect(groupID, 7);
    }

    @Override public void backPress() {
        router.exitGroup();
    }

    @Override
    public void answerStartVoited() {
        delegate.answerStartVoited();
    }

    @Override
    public void answerGetGroupAfterEdit(Group group) {
        router.openGroup(group.id, group.rules);
    }

    public void openGroup(Group group, Router myRouter, Context context) {
        myRouter.getGroup(group, context);
    }

    public void checkListAddUser(){
        groupInteractor.checkListAddUser();
    }

    public void checkListDeleteUser(){
        groupInteractor.checkListDeleteUser(groupID);
    }

    @Override
    public void inputSelect(ArrayList<SelectData> arrayList, String type) {
        Log.i("F", "to respect");
        if (type.equals(constantConfig.ADD)){
            groupInteractor.addSelectUser(arrayList, groupID, userID);
        } else if (type.equals(constantConfig.DELETE)){
            groupInteractor.deleteSelectUser(arrayList, groupID, userID);
        } else if (type.equals(constantConfig.ADMIN)){
            RequestInfo requestInfo = new RequestInfo();
            requestInfo.groupID = groupID;
            requestInfo.groupCreatorID = groupID;
            requestInfo.creatorID = userID;
            requestInfo.userID = arrayList.get(0).id;
            groupInteractor.addAdmin(requestInfo);
        }
    }

    public void startVoited(RequestInfo requestInfo){
        groupInteractor.startVoited(requestInfo);
    }

    public void showAddGroup(){
        RequestInfo requestInfo = new RequestInfo();
        requestInfo.groupCreatorID = groupID;
        requestInfo.creatorID = activity.userID;
        router.showAddGroup(null, requestInfo,this, constantConfig.ADD_GROUP_TYPE);
    }

    public void showEditGroup(RequestInfo requestInfo, Bitmap bitmap){
        InfoProfileData infoProfileData = new InfoProfileData();
        infoProfileData.imageData = bitmap;
        infoProfileData.title = requestInfo.addData.content.simpleData.title;
        infoProfileData.description = requestInfo.addData.content.simpleData.description;
        requestInfo.addData.id = groupID;
        router.showAddGroup(infoProfileData, requestInfo, this, constantConfig.EDIT_GROUP_TYPE);

    }

    public void addAdmin(){
        groupInteractor.checkListAddAdmin();
    }

    public void deleteGroup(RequestInfo requestInfo){
        groupInteractor.deleteGroup(requestInfo);
    }

    @Override
    public void showDialog() {

    }

    @Override
    public void answerDialog(int i) {

    }

    @Override
    public void forResult(Bitmap bitmap) {

    }

    @Override
    public void editInfo(InfoProfileData infoProfileData, RequestInfo requestInfo, String type) {
        if (type.equals(constantConfig.EDIT_GROUP_TYPE)) {
            Bitmap bitmap = infoProfileData.imageData;
            requestInfo.addData = new AddData();
            requestInfo.addData.content.simpleData.title = infoProfileData.title;
            requestInfo.addData.content.simpleData.description = infoProfileData.description;
            requestInfo.addData.id = groupID;
            requestInfo.groupCreatorID = groupID;
            groupInteractor.editGroupPut(bitmap, requestInfo);
        }else if(type.equals(constantConfig.ADD_GROUP_TYPE)){
            Bitmap bitmap = infoProfileData.imageData;
            requestInfo.addData = new AddData();
            requestInfo.addData.content.simpleData.title = infoProfileData.title;
            requestInfo.addData.content.simpleData.description = infoProfileData.description;
            requestInfo.creatorID = userID;
            requestInfo.groupCreatorID = groupID;
            groupInteractor.addSubGroup(bitmap, requestInfo);
        }
    }

    public void exitGroup(RequestInfo requestInfo){
        SelectData selectData = new SelectData();
        selectData.id = userID;
        groupInteractor.exitGroup(requestInfo);
    }


    public void getMembers (String groupID){
        groupInteractor.getUserForList(groupID);
    }
}
