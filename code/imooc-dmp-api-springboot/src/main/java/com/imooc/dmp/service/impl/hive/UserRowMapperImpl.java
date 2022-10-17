package com.imooc.dmp.service.impl.hive;

import com.imooc.dmp.entity.hive.HiveUser;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * RowMapper<User>的实现类
 */
public class UserRowMapperImpl implements RowMapper<HiveUser> {

    @Override
    public HiveUser mapRow(ResultSet resultSet, int i) throws SQLException {
        String user_id = resultSet.getString("user_id");
        String user_sk = resultSet.getString("user_sk");
        String user_lv_cd = resultSet.getString("user_lv_cd");
        int gender = resultSet.getInt("gender");
        int age = resultSet.getInt("age");
        HiveUser hiveUser = new HiveUser();
        hiveUser.setUser_id(user_id);
        hiveUser.setUser_sk(user_sk);
        hiveUser.setUser_lv_cd(user_lv_cd);
        hiveUser.setGender(gender);
        hiveUser.setAge(age);
        return hiveUser;
    }
}
