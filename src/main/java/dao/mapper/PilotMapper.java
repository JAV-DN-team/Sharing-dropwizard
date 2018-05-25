package dao.mapper;

import model.Pilot;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by FRAMGIA\dinh.thanh on 18/05/2018.
 */
public class PilotMapper extends Mapper implements ResultSetMapper<Pilot> {

    @Override
    public Pilot map(int i, ResultSet resultSet, StatementContext statementContext) throws SQLException {
        return getObjectMapper(Pilot.class, resultSet);
    }
}
