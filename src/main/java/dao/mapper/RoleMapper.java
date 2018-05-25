package dao.mapper;

import model.Role;
import model.User;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by FRAMGIA\dinh.thanh on 18/05/2018.
 */
public class RoleMapper extends Mapper implements ResultSetMapper<Role> {

    @Override
    public Role map(int i, ResultSet resultSet, StatementContext statementContext) throws SQLException {
        return getObjectMapper(Role.class, resultSet);
    }
}
