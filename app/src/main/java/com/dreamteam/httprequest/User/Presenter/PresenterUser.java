package com.dreamteam.httprequest.User.Presenter;

import android.graphics.Bitmap;

import com.dreamteam.httprequest.AddOrEditInfoProfile.Data.InfoProfileData;
import com.dreamteam.httprequest.Data.ConstantConfig;
import com.dreamteam.httprequest.Data.QuestionRating.QuestionRating;
import com.dreamteam.httprequest.Data.RequestInfo;
import com.dreamteam.httprequest.Group.Entity.GroupData.Group;
import com.dreamteam.httprequest.MainActivity;
import com.dreamteam.httprequest.ObjectList.ObjectData;
import com.dreamteam.httprequest.User.Protocols.PresenterUserInterface;
import com.dreamteam.httprequest.User.Router;
import com.dreamteam.httprequest.SelectedList.SelectData;
import com.dreamteam.httprequest.User.Entity.UserData.User;
import com.dreamteam.httprequest.User.Interactor.UserInteractor;
import com.dreamteam.httprequest.User.Protocols.ViewUserInterface;

import java.util.ArrayList;

public class PresenterUser implements PresenterUserInterface {

    public ViewUserInterface delegate;
    public MainActivity activity;
    private Router router;

    private UserInteractor userInteractor = new UserInteractor(this);
    private ConstantConfig constantConfig = new ConstantConfig();

    public PresenterUser(ViewUserInterface delegate, MainActivity activity){
        this.delegate = delegate;
        this.activity = activity;
        router = new Router(activity);
    }

    @Override
    public void answerGetUser(User user) {
        delegate.View(user);
    }

    public void answerGetImage (Bitmap bitmap){
        delegate.ViewImage(bitmap);
    }

    @Override
    public void error(Throwable t) {

        delegate.error(t);
    }

    @Override
    public void answerGetGroups(int groups) {
        delegate.answerGetGroups(groups);
    }

    @Override
    public void openUser() {
        router.openProfile();
    }

    @Override
    public void openUserAfterEdit() {
        activity.deleteBackStack();
        router.openProfile();
    }

    @Override public void answerGetGroupsForList(ArrayList<Group> groups) {
        ArrayList<ObjectData> objectDataArrayList = new ArrayList<>();
        for (Group group : groups){
            objectDataArrayList.add(setObjectOfGroup(group));
        }
        router.openGroupList(objectDataArrayList, this,constantConfig.GROUP_TYPE);
    }

    private ObjectData setObjectOfGroup(Group group){
        ObjectData objectData = new ObjectData();
        objectData.id = group.id;
        objectData.title = group.content.simpleData.title;
        objectData.description = group.content.simpleData.description;
        objectData.image = group.content.mediaData.image;
        return objectData;
    }

    @Override
    public void answerGetRating(ArrayList<QuestionRating> questionRatings) {
        delegate.answerGetRating(questionRatings);
    }

    public void getUser(String id){
        userInteractor.getUser(id);
    }

    public void postUser(String name, String surname){
        userInteractor.postUser(name, surname);
    }

    //отправка на объекта на изменение
    public void showEditProfile(User user, Bitmap bitmap){
        InfoProfileData infoProfileData = new InfoProfileData();
        infoProfileData.id = user.id;
        infoProfileData.title = user.content.simpleData.name;
        infoProfileData.description = user.content.simpleData.surname;
        infoProfileData.imageData = bitmap;
        router.showEditInfoProfile(infoProfileData,this, constantConfig.USER_TYPE);
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
    public void inputSelect(ArrayList<SelectData> arrayList, String type) {

    }

    //получение измененных данных и отпрака их в Interactor
    @Override
    public void editInfo(InfoProfileData infoProfileData, RequestInfo requestInfo, String type) {
        User user = new User();
        user.id = infoProfileData.id;
        user.content.simpleData.name = infoProfileData.title;
        user.content.simpleData.surname = infoProfileData.description;
        Bitmap bitmap = infoProfileData.imageData;

        userInteractor.putUser(user, bitmap);
    }

    public void getGroups(String userID){
        userInteractor.getGroupForList(userID);
    }

    public void getRating (String userID){
        userInteractor.getRating(userID);
    }
}
