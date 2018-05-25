package oauth2.auth;

import io.dropwizard.auth.Authorizer;
import model.User;

/**
 * Created by FRAMGIA\dinh.thanh on 25/05/2018.
 */
public class OAuth2Authorizer implements Authorizer<User> {

    @Override
    public boolean authorize(User user, String s) {
        return user.getRoles().contains(s);
    }
}
