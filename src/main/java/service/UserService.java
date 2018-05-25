package service;

import dao.RoleDao;
import dao.UserDao;
import model.User;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.CreateSqlObject;

/**
 * Created by FRAMGIA\dinh.thanh on 18/05/2018.
 */
public abstract class UserService {

    @CreateSqlObject
    abstract UserDao userDao();

    @CreateSqlObject
    abstract RoleDao roleDao();

    public User getUser(String username, String password) {
        User user = userDao().getUser(username, password);
        if (user != null) {
            user.setRoles(roleDao().getRoles(user.getId()));
        }

        return user;
    }

    public void createUser(User user) {
        userDao().createUser(user);
    }

}
