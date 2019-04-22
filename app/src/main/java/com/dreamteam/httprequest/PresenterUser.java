import com.dreamteam.httprequest.PresenterInterface;
import com.dreamteam.httprequest.User.User;
import com.dreamteam.httprequest.UserInteractor;

public class PresenterUser implements PresenterInterface {

    UserInteractor userInteractor = new UserInteractor();

    public PresenterUser(){
        userInteractor.presenterDelegate = this;
    }

    @Override
    public void answerGetUser(User user) {

    }

    public void getUser(String id){
        userInteractor.getUser(id);
    }
}
