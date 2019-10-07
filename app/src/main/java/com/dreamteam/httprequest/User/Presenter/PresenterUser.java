package com.dreamteam.httprequest.User.Presenter;

import android.graphics.Bitmap;
import android.util.Log;

import com.dreamteam.httprequest.AddOrEditInfoProfile.Data.InfoProfileData;
import com.dreamteam.httprequest.Data.ConstantConfig;
import com.dreamteam.httprequest.Data.QuestionRating.QuestionRating;
import com.dreamteam.httprequest.Data.RequestInfo;
import com.dreamteam.httprequest.Event.Entity.Events.EventsKinds.Rating;
import com.dreamteam.httprequest.Group.Entity.GroupData.Group;
import com.dreamteam.httprequest.MainActivity;
import com.dreamteam.httprequest.ObjectList.ObjectData;
import com.dreamteam.httprequest.User.Entity.UserData.RatingData.RatingData;
import com.dreamteam.httprequest.User.Protocols.PresenterUserInterface;
import com.dreamteam.httprequest.User.Router;
import com.dreamteam.httprequest.SelectedList.Data.SelectData;
import com.dreamteam.httprequest.User.Entity.UserData.User;
import com.dreamteam.httprequest.User.Interactor.UserInteractor;
import com.dreamteam.httprequest.User.Protocols.ViewUserInterface;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
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
        objectData.title = group.personal.descriptive.title;
//        objectData.description = group.content.simpleData.description;
//        objectData.image = group.content.mediaData.image;
        return objectData;
    }

    @Override
    public void answerGetRating(RatingData rating) {
        delegate.answerGetRating(rating);
    }

    public void getUser(String id){
        userInteractor.getUser(id);
        userInteractor.getImageThis(id);
    }

    public void postUser(String name, String surname){
        userInteractor.postUser(name, surname);
    }

    //отправка на объекта на изменение
    public void showEditProfile(User user, Bitmap bitmap){
        InfoProfileData infoProfileData = new InfoProfileData();
        infoProfileData.id = user.id;
        infoProfileData.title = user.personal.descriptive.name;
        infoProfileData.description = user.personal.descriptive.surname;
        infoProfileData.imageData = bitmap;
        router.showEditInfoProfile(infoProfileData,this, constantConfig.USER_TYPE);
     }

    @Override
    public void showDialog() {

    }

    @Override
    public void answerDialog(int i, String title, String message, String priority) {

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
        File filesDir = activity.getFilesDir();
        File imageFile = new File(filesDir, "file" + ".jpg");
        user.id = infoProfileData.id;
        user.personal.descriptive.name = infoProfileData.title;
        user.personal.descriptive.surname = infoProfileData.description;
        Bitmap bitmap = infoProfileData.imageData;


        OutputStream os;
        try {
            os = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
            os.flush();
            os.close();
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Error writing bitmap", e);
        }


        userInteractor.postEditUser(user, imageFile);
    }



    public void getGroups(String userID){
        userInteractor.getGroupForList(userID);
    }

    public void getRating (String userID){
        userInteractor.getRating(userID);
    }
}
