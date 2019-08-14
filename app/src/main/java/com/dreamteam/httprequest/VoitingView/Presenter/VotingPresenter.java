package com.dreamteam.httprequest.VoitingView.Presenter;

import android.graphics.Bitmap;

import com.dreamteam.httprequest.Event.Entity.AnswersVoting.AnswersVoting;
import com.dreamteam.httprequest.User.Entity.UserData.User;
import com.dreamteam.httprequest.User.Protocols.VotingPresenterInterface;
import com.dreamteam.httprequest.User.Protocols.VotingViewInterface;
import com.dreamteam.httprequest.VoitingView.Interactor.VotingInteractor;

public class VotingPresenter implements VotingPresenterInterface {

    private VotingInteractor votingInteractor = new VotingInteractor(this);
    private VotingViewInterface delegate;
    private String votingId;

    public VotingPresenter (VotingViewInterface delegate){
        this.delegate = delegate;
    }

    public void getUsersForVoting(String votingId){
        votingInteractor.getUsersForVoting(votingId);
        this.votingId = votingId;
    }

    @Override
    public void answerGetUser(User user) {
        votingInteractor.getImage(user.id);
        delegate.answerGetUser(user);
    }

    @Override
    public void answerGetImage(Bitmap bitmap) {
        delegate.answerGetImage(bitmap);
    }

    @Override
    public void prepareAnswerVoting() {
        getUsersForVoting(votingId);
    }

    @Override
    public void notUsers() {
        delegate.notUsers();
    }

    public void setVoitingAnswer(AnswersVoting answersVoting){
        votingInteractor.setVoitingAnswer(answersVoting);
    }


}
