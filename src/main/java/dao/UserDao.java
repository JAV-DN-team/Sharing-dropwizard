package dao;

import dao.mapper.UserMapper;
import model.User;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

/**
 * Created by FRAMGIA\dinh.thanh on 18/05/2018.
 */
@RegisterMapper(UserMapper.class)
public interface UserDao {

    @SqlQuery("select * from user where username = :username and password = :password")
    User getUser(@Bind("username") String username, @Bind("password") String password);

    @SqlUpdate("insert into user(username, password) values(:username, :password)")
    void createUser(@BindBean User User);
}
