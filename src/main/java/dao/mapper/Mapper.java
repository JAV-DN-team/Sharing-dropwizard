package dao.mapper;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

/**
 * Created by FRAMGIA\dinh.thanh on 18/05/2018.
 */
public class Mapper {

    <T> T getObjectMapper(Class<T> clazz, ResultSet resultSet) {

        T obj = null;

        try {
            obj = clazz.newInstance();
            for (Field f : obj.getClass().getDeclaredFields()) {
                f.setAccessible(true);
                Object value;

                if (f.getType() == Long.class) {
                    value = resultSet.getLong(f.getName());
                } else if (f.getType() == Date.class) {
                    value = resultSet.getDate(f.getName());
                } else if (f.getType() == String.class) {
                    value = resultSet.getString(f.getName());
                } else {
                    value = null;
                }

                f.set(obj, value);
            }
        } catch (InstantiationException | IllegalAccessException | SQLException e) {
            e.printStackTrace();
        }

        return obj;
    }
}
