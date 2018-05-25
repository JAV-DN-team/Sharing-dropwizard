package dao;

import dao.mapper.RoleMapper;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import java.util.Set;

/**
 * Created by FRAMGIA\dinh.thanh on 18/05/2018.
 */
@RegisterMapper(RoleMapper.class)
public interface RoleDao {

    @SqlQuery("select role from role where id = :id")
    Set<String> getRoles(@Bind("id") Long id);
}
