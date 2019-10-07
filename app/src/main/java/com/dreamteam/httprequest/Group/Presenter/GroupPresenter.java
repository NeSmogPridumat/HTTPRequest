package com.dreamteam.httprequest.Group.Presenter;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.dreamteam.httprequest.AddOrEditInfoProfile.Data.InfoProfileData;
import com.dreamteam.httprequest.Data.AddData;
import com.dreamteam.httprequest.Data.ConstantConfig;
import com.dreamteam.httprequest.Data.RequestInfo;
import com.dreamteam.httprequest.Dialog.DialogConfig;
import com.dreamteam.httprequest.Event.Entity.EventType4.EventType4;
import com.dreamteam.httprequest.Event.Entity.Events.EventsKinds.DataEvents.Event;
import com.dreamteam.httprequest.Event.Entity.Events.EventsObject;
import com.dreamteam.httprequest.Event.Entity.InfoStartEvent.InfoStartEvent;
import com.dreamteam.httprequest.Event.Interactor.GetterEvents;
import com.dreamteam.httprequest.Group.Entity.GroupData.EditGroupData.EditGroupData;
import com.dreamteam.httprequest.Group.Entity.GroupData.Group;
import com.dreamteam.httprequest.Group.Entity.GroupData.Personal;
import com.dreamteam.httprequest.Group.GroupRouter;
import com.dreamteam.httprequest.Group.Interactor.GroupInteractor;
import com.dreamteam.httprequest.Group.Protocols.GroupPresenterInterface;
import com.dreamteam.httprequest.Group.Protocols.GroupViewInterface;
import com.dreamteam.httprequest.GroupList.Protocols.Router;
import com.dreamteam.httprequest.MainActivity;
import com.dreamteam.httprequest.ObjectList.ObjectData;
import com.dreamteam.httprequest.R;
import com.dreamteam.httprequest.SelectedList.Data.SelectData;
import com.dreamteam.httprequest.User.Entity.UserData.User;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.EventObject;

public class GroupPresenter implements GroupPresenterInterface {
    private GroupViewInterface delegate;
    private GroupRouter router;
    private GroupInteractor groupInteractor = new GroupInteractor(this);
    private String groupID;
    private String userID;
    private ConstantConfig constantConfig = new ConstantConfig();
    private MainActivity activity;
    private EventsObject eventObject;
    private int countRating, countDiscussion, countPoll;

    private DialogConfig dialogConfig;

    public GroupPresenter(GroupViewInterface delegate, MainActivity activity){
        this.delegate = delegate;
        this.activity = activity;
        router = new GroupRouter(activity);
        dialogConfig = new DialogConfig(activity);
    }

    //============================ОТПРАВКА В INTERACTOR==========================================//

    public void getGroup(String id){
        userID = activity.userID;
        groupInteractor.getGroup(id, userID);
        groupID = id;
    }

    @Override
    public void error(Throwable t) {
        delegate.error(t);
    }

    @Override
    public void answerGetGroup(Group group) {
        delegate.outputGroupView(group);
    }

    @Override
    public void answerGetImage(Bitmap bitmap) {
        delegate.outputImageView(bitmap);
    }

//    @Override
//    public void answerGetMembers(ArrayList<User> members, String type) {
//        delegate.outputMembersView(members);
//    }

    @Override
    public void answerGetMembersForList(ArrayList<User> arrayList) {
        ArrayList<ObjectData> objectDataArrayList = new ArrayList<>();
        for(User user : arrayList){
            ObjectData objectData = new ObjectData();
            objectData.id = user.id;
            objectData.title = user.personal.descriptive.name + " " + user.personal.descriptive.surname;
            objectDataArrayList.add(objectData);
        }
        router.showMembersList(objectDataArrayList, this, constantConfig.USER_TYPE);
    }

    @Override
    public void answerGetUsersForSelectAdd(ArrayList<User> users) {
       delegate.answerGetUsersForSelectAdd(users);
    }

    public void setUserSelect(ArrayList<User> users){
        ArrayList<SelectData> selectData = new ArrayList<>();
        for (int i = 0; i < users.size(); i++){
            selectData.add(new SelectData().initFromUser(users.get(i)));
        }
        router.showSelectList(selectData, this, constantConfig.ADD);
    }

    @Override
    public void answerGetUsersForSelectAdmin(ArrayList<User> users) {
        ArrayList<SelectData> selectData = new ArrayList<>();
        for (int i = 0; i < users.size(); i++){
            selectData.add(new SelectData().initFromUser(users.get(i)));
        }
        router.showSelectList(selectData, this, constantConfig.ADMIN);
    }

    @Override
    public void answerGetUsersForSelectDelete(ArrayList<User> users) {
        ArrayList<SelectData> selectData = new ArrayList<>();
        for (int i = 0; i < users.size(); i++){
            if(!(userID.equals(users.get(i).id))) {
                selectData.add(new SelectData().initFromUser(users.get(i)));
            }
        }
        router.showSelectList(selectData, this, constantConfig.DELETE);
    }

    @Override
    public void openGroupsList() {
        router.openGroupsList();
    }

    @Override
    public void answerAddGroup(String groupId) {
        activity.deleteBackStack();
        router.openGroup(groupId);
//        groupInteractor.getGroupAfterEdit(event.data.groupCreatorID, userID);
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
        router.openGroup(group.id);
    }

    @Override
    public void answerGetSubgroup(ArrayList<Group> subgroups) {
        ArrayList<ObjectData> objectDataList = new ArrayList<>();
        for (Group subgroup : subgroups){
            ObjectData objectData = new ObjectData();
            objectData.id = subgroup.id;
            objectData.title = subgroup.personal.descriptive.title;
            objectData.admin = subgroup.admin;
            objectDataList.add(objectData);
        }
        router.openObjectList(objectDataList, this);
    }

    @Override
    public void backPressAfterSelectAdmin() {
        int[] photoActionArray = {dialogConfig.OK_CODE};
        router.showDialog("Ваша заявка отправлена на подтверждение", photoActionArray, this);//TODO
        activity.deleteBackStack();
        getGroup(groupID);
    }

    @Override
    public void answerGetEvents(EventsObject eventsObject) {
        countRating = eventsObject.events.ratings.size();
        countDiscussion = eventsObject.events.discussions.size();
        countPoll = eventsObject.events.polls.size();
        for (int i = 0; i < eventsObject.events.ratings.size(); i++){
            GetterEvents getterEvents = new GetterEvents(this);
            getterEvents.setEvent(eventsObject.events.ratings.get(i), constantConfig.EVENT_TYPE_RATING);
        }
        for (int i = 0; i < eventsObject.events.discussions.size(); i++){
            GetterEvents getterEvents = new GetterEvents(this);
            getterEvents.setEvent(eventsObject.events.discussions.get(i), constantConfig.EVENT_TYPE_DISCUSSION);
        }
        for (int i = 0; i < eventsObject.events.polls.size(); i++){
            GetterEvents getterEvents = new GetterEvents(this);
            getterEvents.setEvent(eventsObject.events.polls.get(i), constantConfig.EVENT_TYPE_POLL);
        }

    }

    @Override
    public void answerGetterEvent(Event event, String type) {
        if (type.equals(constantConfig.EVENT_TYPE_RATING)){
            eventObject.events.ratings.add(event);
        } else if (type.equals(constantConfig.EVENT_TYPE_DISCUSSION)){
            eventObject.events.discussions.add(event);
        } else if (type.equals(constantConfig.EVENT_TYPE_POLL)){
            eventObject.events.polls.add(event);
        }

        if (eventObject.events.ratings.size() == countRating && eventObject.events.polls.size() == countPoll && eventObject.events.discussions.size() == countDiscussion){
            Handler mainHandler = new Handler(Looper.getMainLooper());
            Runnable myRunnable = new Runnable() {
                @Override
                public void run() {
                    delegate.answerGetEvents(eventObject);
                }
            };
            mainHandler.post(myRunnable);

        }
    }

    @Override
    public void answerStartDiscussion() {
        delegate.answerStartDiscussion();
    }

    public void openGroup(Group group, Router myRouter, Context context) {
        myRouter.getGroup(group, context);
    }

    public void checkListAddUser(){
//        router.showSelectList(this);
        groupInteractor.checkListAddUser(groupID);
    }

    public void checkListDeleteUser(ArrayList<String> members){
        groupInteractor.checkListDeleteUser(members);
    }

    @Override
    public void inputSelect(ArrayList<SelectData> arrayList, String type) {
        if (type.equals(constantConfig.ADD)){
            groupInteractor.addSelectUser(arrayList, groupID, userID);
        } else if (type.equals(constantConfig.DELETE)){
            groupInteractor.deleteSelectUser(arrayList, groupID, userID);
        } else if (type.equals(constantConfig.ADMIN)){
//            RequestInfo requestInfo = new RequestInfo();
//            requestInfo.groupID = groupID;
//            requestInfo.groupCreatorID = groupID;
//            requestInfo.creatorID = userID;
//            requestInfo.userID = arrayList.get(0).id;

            EditGroupData editGroupData = new EditGroupData();
            editGroupData.admin = arrayList.get(0).id;
            groupInteractor.addAdmin(editGroupData, groupID);
        }
    }

    public void startVoited(String groupId){
        groupInteractor.startVoited(groupId);
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

    public void addAdmin(ArrayList<String> members){
        groupInteractor.checkListAddAdmin(members);
    }

    public void deleteGroup(String groupId){
        groupInteractor.deleteGroup(groupId);
    }

    @Override
    public void showDialog() {

    }

    @Override
    public void answerDialog(int i, String title, String message, String priority) {
        if (i == -1){
            Log.i("ЫЫЫЫЫЫЫЫЫЫЫЫЫЫ", message);
            groupInteractor.startDiscussion(groupID, title, message, priority);
        }
    }

    @Override
    public void forResult(Bitmap bitmap) {

    }

    @Override
    public void editInfo(InfoProfileData infoProfileData, RequestInfo requestInfo, String type) {
        if (type.equals(constantConfig.EDIT_GROUP_TYPE)) {
            Bitmap bitmap = infoProfileData.imageData;
//            requestInfo.addData = new AddData();
//            requestInfo.addData.content.simpleData.title = infoProfileData.title;
//            requestInfo.addData.content.simpleData.description = infoProfileData.description;
//            requestInfo.addData.id = groupID;
//            requestInfo.groupCreatorID = groupID;

//            Group group = new Group();
//            group.personal = new Personal();
//            group.personal.descriptive.title = infoProfileData.title;
//            group.id = requestInfo.groupCreatorID;


            EditGroupData editGroupData = new EditGroupData();
            editGroupData.personal = new Personal();
            editGroupData.personal.descriptive.title = infoProfileData.title;
            editGroupData.personal.descriptive.description = infoProfileData.description;

            File imageFile = getFileinBitmap(bitmap);

            groupInteractor.editGroupPost(groupID, editGroupData, imageFile);
        }else if(type.equals(constantConfig.ADD_GROUP_TYPE)){


            //создание подгруппы
            Group group = new Group();
            group.personal = new Personal();
            group.personal.descriptive.title = infoProfileData.title;
            group.personal.parent = requestInfo.groupCreatorID;
            group.personal.descriptive.description = infoProfileData.description;
            //group.content.simpleData.description = infoProfileData.description;
//            if (requestInfo == null) {
//                requestInfo = new RequestInfo();
//            }
//            requestInfo.creatorID = activity.userID;
//
            Bitmap bitmap = infoProfileData.imageData;
            File imageFile = getFileinBitmap(bitmap);

            groupInteractor.addSubGroup(group, imageFile);


//            Bitmap bitmap = infoProfileData.imageData;
//            requestInfo.addData = new AddData();
//            requestInfo.addData.content.simpleData.title = infoProfileData.title;
//            requestInfo.addData.content.simpleData.description = infoProfileData.description;
//            requestInfo.creatorID = userID;
//            requestInfo.groupCreatorID = groupID;
//            groupInteractor.addSubGroup(bitmap, requestInfo);
        } else {


        }
    }

    public void exitGroup(EditGroupData editGroupData){
        groupInteractor.exitGroup(groupID, editGroupData);
    }

    public void getMembers (ArrayList<String> members){
        groupInteractor.getUserForList(members);
    }

    public void getSubgroup(ArrayList<String> subgroups){
        groupInteractor.getSubgroup(subgroups, userID);
    }

    public void startCreateDiscussionFragment(String groupID){
        router.openCreateDiscussionFragment(this);
    }

    public void getEvents(){
        eventObject = new EventsObject();
        groupInteractor.getEvents(groupID);
    }

    @Override
    public void errorHading(int resposeCode, String type) {
        if (resposeCode == 403 && type.equals(constantConfig.SET_USER_IN_GROUP_TYPE)) {
            String title = activity.getResources().getString(R.string.add_error);
            String description = activity.getResources().getString(R.string.user_invited_to_group);
            delegate.errorHanding(title, description);
        }else if(resposeCode == 403 && type.equals(constantConfig.SET_DELETE_USER_IN_GROUP_TYPE)) {
            String title = activity.getResources().getString(R.string.delete_error);
            String description = activity.getResources().getString(R.string.deletion_will_be_available_after_the_event_is_completed);
            delegate.errorHanding(title, description);
        } else if(resposeCode == 403 && type.equals((constantConfig.ADD_ADMIN))){
            String title = ("Ошибка");
            String description = ("Убрать себя из админов");
            delegate.errorHanding(title, description);
        }
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

    public void openVoting(String idRatingEvent){
        router.openVoting(idRatingEvent);
    }
}
