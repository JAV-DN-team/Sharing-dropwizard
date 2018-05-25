package dao.mapper;

import model.User;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by FRAMGIA\dinh.thanh on 18/05/2018.
 */
public class UserMapper extends Mapper implements ResultSetMapper<User> {

    @Override
    public User map(int i, ResultSet resultSet, StatementContext statementContext) throws SQLException {
        return getObjectMapper(User.class, resultSet);
    }
}
