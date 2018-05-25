package dao;

import dao.mapper.Mapper;
import dao.mapper.PilotMapper;
import model.Pilot;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import java.util.List;

/**
 * Created by FRAMGIA\dinh.thanh on 18/05/2018.
 */
@RegisterMapper(PilotMapper.class)
public interface PilotDao {

    @SqlUpdate("create table pilot"
            + "(id int primary key,"
            + " name varchar(100),"
            + " info varchar(255),"
            + " level varchar(50))")
    void createTable();

    @SqlQuery("select * from pilot")
    List<Pilot> getPilots();

    @SqlQuery("select * from pilot where id = :id")
    Pilot getPilot(@Bind("id") Long id);

    @SqlUpdate("insert into pilot(name, info, level) values(:name, :info, :level)")
    void createPilot(@BindBean Pilot Pilot);

    @SqlUpdate("update pilot set name = coalesce(:name, name), info = coalesce(:info, info), level = coalesce(:level, level) where id = :id")
    void editPilot(@BindBean Pilot Pilot);

    @SqlUpdate("delete from pilot where id = :id")
    void deletePilot(@Bind("id") Long id);

    @SqlQuery("select last_insert_id();")
    Long lastInsertId();
}
