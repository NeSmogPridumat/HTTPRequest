package com.dreamteam.httprequest.Group.Presenter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.dreamteam.httprequest.AddOrEditInfoProfile.InfoProfileData;
import com.dreamteam.httprequest.Data.AddData;
import com.dreamteam.httprequest.Data.ConstantConfig;
import com.dreamteam.httprequest.Data.RequestInfo;
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
    private int rules;
    private ConstantConfig constantConfig = new ConstantConfig();
    private MainActivity activity;

    private final String ADD = "Add";
    private final String DELETE = "Delete";
    private final String ADMIN = "Admin";


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
        router.showMembersList(arrayList, this, "User");
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
    }

    @Override
    public void answerAddGroup(Group group) {
        router.openGroup(group.id, group.rules);
    }

    @Override
    public void openGroupAfterSelect() {
        router.openGroupAfterSelect(groupID, rules);
    }

    @Override public void backPress() {
        router.exitGroup();
    }

    @Override
    public void answerStartVoited() {
        delegate.answerStartVoited();
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
        if (type.equals(ADD)){
            groupInteractor.addSelectUser(arrayList, groupID, userID);
        } else if (type.equals(DELETE)){
            groupInteractor.deleteSelectUser(arrayList, groupID, userID);
        } else if (type.equals(ADMIN)){

        }
    }

    public void startVoited(RequestInfo requestInfo){
        groupInteractor.startVoited(requestInfo);
    }

    public void showAddGroup(){
        RequestInfo requestInfo = new RequestInfo();
        requestInfo.groupCreatorID = groupID;
        requestInfo.creatorID = activity.userID;
        router.showAddGroup(null, requestInfo,this, "Group");
    }

    public void showEditGroup(RequestInfo requestInfo, Bitmap bitmap){
        InfoProfileData infoProfileData = new InfoProfileData();
        infoProfileData.imageData = bitmap;
        infoProfileData.title = requestInfo.addData.content.simpleData.title;
        infoProfileData.description = requestInfo.addData.content.simpleData.description;
        requestInfo.addData.id = groupID;
        router.showAddGroup(infoProfileData, requestInfo, this, "Group");

    }

    public void addAdmin(){
//        router.addAdminSelect();
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
    public void editInfo(InfoProfileData infoProfileData, RequestInfo requestInfo) {
        Bitmap bitmap = infoProfileData.imageData;
        requestInfo.addData = new AddData();
        requestInfo.addData.content.simpleData.title = infoProfileData.title;
        requestInfo.addData.content.simpleData.description = infoProfileData.description;
        requestInfo.groupID = groupID;
        requestInfo.groupCreatorID = null;
        groupInteractor.addOrEditGroup(bitmap, requestInfo);
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
