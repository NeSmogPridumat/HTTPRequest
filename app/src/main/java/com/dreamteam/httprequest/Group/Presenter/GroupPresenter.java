package com.dreamteam.httprequest.Group.Presenter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.dreamteam.httprequest.AddOrEditInfoProfile.InfoProfileData;
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
    private Router myRouter;
    private GroupRouter router;
    private GroupInteractor groupInteractor = new GroupInteractor(this);
    private String groupID;
    private String userID;
    private ConstantConfig constantConfig = new ConstantConfig();

    private final String ADD = "Add";
    private final String DELETE = "Delete";
    private final String ADMIN = "Admin";


    public GroupPresenter(GroupViewInterface delegate, MainActivity activity){
        this.delegate = delegate;
        router = new GroupRouter(activity);
        userID = activity.userID;
    }

    //============================ОТПРАВКА В INTERACTOR==========================================//

    public void getGroup(String id){
        groupInteractor.getGroup(id);
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
    public void answerGetMembers(int members, String type) {
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
    public void openGroup() {
        router.openGroup(groupID);
    }

    @Override
    public void openGroupsList() {
        router.openGroupsList();
    }

    @Override
    public void answerAddGroup(Group group) {
        router.openGroup(group.id);
    }

    @Override
    public void openGroupAfterSelect() {
        router.openGroupAfterSelect(groupID);
    }

    @Override public void backPress() {
        router.exitGroup();
    }

    public void openGroup(Group group, Router myRouter, Context context) {
        myRouter.getGroup(group, context);
        myRouter=null;
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
            groupInteractor.addSelectUser(arrayList, groupID);
        } else if (type.equals(DELETE)){
            groupInteractor.deleteSelectUser(arrayList, groupID);
        } else if (type.equals(ADMIN)){

        }

    }

    public void showAddGroup(){
        InfoProfileData infoProfileData = null;
        router.showAddGroup(infoProfileData, this, "Group");
    }

    public void addAdmin(){
//        router.addAdminSelect();
        groupInteractor.checkListAddAdmin();
    }

    public void deleteGroup(Group group){
        groupInteractor.deleteGroup(group);
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
        Group group = new Group();
        group.content.simpleData.title = infoProfileData.title;
        group.content.simpleData.description = infoProfileData.description;

        Bitmap bitmap = infoProfileData.imageData;
        groupInteractor.addGroup(group, bitmap, requestInfo);
    }

    public void exitGroup(){
        SelectData selectData = new SelectData();
        selectData.id = userID;
        groupInteractor.exitGroup(selectData, groupID);
    }

    public void getMembers (String groupID){
        groupInteractor.getUserForList(groupID);
    }
}
